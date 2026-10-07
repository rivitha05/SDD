package com.sdd.assessment.core;

import com.microsoft.playwright.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/** PicoContainer creates one World per scenario. No static mutable driver or shared test data. */
public final class World implements AutoCloseable {
    public Playwright playwright;
    public Browser browser;
    public BrowserContext context;
    public Page page;
    public AndroidDriver mobile;
    private Path firefoxProfile;
    public void startWeb() {
        playwright = Playwright.create(new Playwright.CreateOptions().setEnv(java.util.Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1")));
        BrowserType type = switch (Config.get("web.browser")) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new IllegalArgumentException("Unsupported web.browser");
        };
        BrowserType.LaunchOptions launch = new BrowserType.LaunchOptions().setHeadless(Config.bool("web.headless"));
        String proxy = System.getenv("HTTPS_PROXY");
        if (proxy != null && !proxy.isBlank()) launch.setProxy(new com.microsoft.playwright.options.Proxy(proxy).setBypass("localhost,127.0.0.1"));
        String trustStore = Config.get("web.firefoxTrustStore");
        if (Config.get("web.browser").equals("firefox") && !trustStore.isBlank()) {
            // Only the NSS trust databases are copied; cookies and browsing state are never shared.
            try {
                Path source = Path.of(trustStore).toAbsolutePath();
                if (!Files.isRegularFile(source.resolve("cert9.db"))) {
                    throw new IllegalArgumentException("Firefox trust store requires cert9.db: " + source);
                }
                firefoxProfile = Files.createTempDirectory("sdd-firefox-");
                for (String file : new String[] {"cert9.db", "key4.db", "pkcs11.txt"}) {
                    if (Files.isRegularFile(source.resolve(file))) Files.copy(source.resolve(file), firefoxProfile.resolve(file));
                }
                var options = new BrowserType.LaunchPersistentContextOptions()
                    .setHeadless(Config.bool("web.headless")).setViewportSize(1440, 1000)
                    .setTimezoneId(Config.get("web.timezone"));
                if (launch.proxy != null) options.setProxy(launch.proxy);
                context = type.launchPersistentContext(firefoxProfile, options);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException("Cannot prepare isolated Firefox trust profile", e);
            }
        } else {
            browser = type.launch(launch);
            context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 1000).setTimezoneId(Config.get("web.timezone")));
        }
        context.setDefaultTimeout(Config.positiveInt("timeout.seconds") * 1000.0);
        context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        page = context.newPage();
    }
    public void startMobile() throws Exception {
        Path app = Path.of(Config.get("mobile.app")).toAbsolutePath();
        if (!Files.isRegularFile(app)) throw new IllegalStateException("APK not found: " + app);
        Path runtimeApk = Path.of("target", "runtime-apk", java.util.UUID.randomUUID() + ".apk").toAbsolutePath();
        Files.createDirectories(runtimeApk.getParent());
        Files.copy(app, runtimeApk);
        // Appium may re-sign the installation copy; keep the supplied artifact byte-for-byte intact.
        UiAutomator2Options options = new UiAutomator2Options()
            .setDeviceName(Config.get("mobile.deviceName")).setApp(runtimeApk.toString())
            .setAppPackage(Config.get("mobile.package")).setAppActivity(Config.get("mobile.activity"))
            .setLanguage("en").setLocale("US").setAutoGrantPermissions(true)
            .setNewCommandTimeout(Duration.ofSeconds(120)).setAdbExecTimeout(Duration.ofSeconds(120))
            .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(120)).setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(120));
        if (!Config.get("mobile.udid").isEmpty()) options.setUdid(Config.get("mobile.udid"));
        if (!Config.get("mobile.chromedriverExecutable").isEmpty()) options.setChromedriverExecutable(Path.of(Config.get("mobile.chromedriverExecutable")).toAbsolutePath().toString());
        mobile = new AndroidDriver(URI.create(Config.get("mobile.serverUrl")).toURL(), options);
        mobile.manage().timeouts().implicitlyWait(Duration.ZERO);
        // Android 9 shows a first-launch compatibility notice for this legacy APK.
        // Dismiss only that exact notice, never ANRs, crash dialogs, or arbitrary app alerts.
        new org.openqa.selenium.support.ui.WebDriverWait(mobile, Duration.ofSeconds(Config.positiveInt("mobile.timeoutSeconds")))
            .until(d -> {
                var notices = mobile.findElements(org.openqa.selenium.By.id("android:id/message"));
                if (notices.stream().anyMatch(e -> e.getText().startsWith("This app was built for an older version of Android"))) {
                    mobile.findElement(org.openqa.selenium.By.id("android:id/button1")).click();
                }
                return mobile.findElements(io.appium.java_client.AppiumBy.androidUIAutomator(
                    "new UiSelector().text(\"" + Config.get("mobile.title") + "\")")).stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
            });
    }
    @Override public void close() {
        try { if (mobile != null) mobile.quit(); }
        finally {
            try { if (context != null) context.close(); }
            finally {
                try { if (browser != null) browser.close(); }
                finally {
                    try { if (playwright != null) playwright.close(); }
                    finally {
                        if (firefoxProfile != null) {
                            try (var files = Files.walk(firefoxProfile)) {
                                for (Path file : files.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(file);
                            } catch (java.io.IOException e) {
                                throw new java.io.UncheckedIOException("Cannot remove Firefox test profile", e);
                            }
                        }
                    }
                }
            }
        }
    }
}
