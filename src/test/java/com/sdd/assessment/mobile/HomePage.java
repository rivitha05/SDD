package com.sdd.assessment.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.testng.Assert.*;

public final class HomePage extends NativePage {
    public HomePage(AndroidDriver driver) { super(driver); }
    public void verify() {
        title();
        assertTrue(driver.currentActivity().endsWith("HomeScreenActivity"), "Home activity");
        for (String name : new String[] {"buttonTest", "buttonStartWebview", "startUserRegistration", "my_text_field", "waitingButtonTest", "input_adds_check_box", "visibleButtonTest", "showToastButton", "showPopupWindowButton", "exceptionTestButton", "exceptionTestField"}) {
            assertTrue(visible(id(name)).isEnabled(), "Home control " + name);
        }
        assertEquals(visible(id("buttonTest")).getText(), "EN Button");
    }
    public void cancelExit() {
        visible(id("buttonTest")).click();
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"No, no\")")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(AppiumBy.id("android:id/button2")));
        verify();
    }
    public void registration() { visible(id("startUserRegistration")).click(); }
    public void webview() { visible(id("buttonStartWebview")).click(); }
    public void progress() {
        visible(id("waitingButtonTest")).click();
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Waiting Dialog\")"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(AppiumBy.androidUIAutomator("new UiSelector().text(\"Waiting Dialog\")")));
        new RegistrationPage(driver).defaultsAndControls();
    }
    public void toast() {
        driver.setSetting("enableMultiWindows", true);
        driver.setSetting("enableNotificationListener", true);
        Object previousIdle = driver.getSettings().get("waitForIdleTimeout");
        driver.setSetting("waitForIdleTimeout", 0);
        try {
            // Initialize the XPath engine before the short-lived notification appears.
            driver.findElements(AppiumBy.xpath("//android.widget.Button"));
            visible(id("showToastButton")).click();
            // Short polling is necessary for the transient toast; no fixed sleeps.
            var toastWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5));
            toastWait.pollingEvery(java.time.Duration.ofMillis(100));
            // Assert the exact text in one server-side lookup: a second getText can outlive the toast.
            assertNotNull(toastWait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//android.widget.Toast[@text='Hello selendroid toast!']"))),
                "Toast must expose the exact expected notification text");
        } finally { driver.setSetting("waitForIdleTimeout", previousIdle); }
    }
    public void popup() {
        // The legacy non-focusable PopupWindow is outside the active application window.
        driver.setSetting("enableMultiWindows", true);
        visible(id("showPopupWindowButton")).click();
        visible(id("popup_dismiss_button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(id("popup_dismiss_button")));
        verify();
    }
    public void crashButton() { visible(id("exceptionTestButton")).click(); }
    public void crashText() {
        try { visible(id("exceptionTestField")).sendKeys("test"); }
        catch (org.openqa.selenium.StaleElementReferenceException e) {
            // This app crashes synchronously in its text listener, before sendKeys can reread the input.
            // Accept only that interrupted trigger after the app has left home; the next assertion still fails.
            if (driver.currentActivity().endsWith("HomeScreenActivity")) throw e;
            io.qameta.allure.Allure.addAttachment("Text trigger interrupted by app exit", "text/plain", e.getMessage(), ".txt");
        }
    }
    public void assertStillHomeAfterCrash() {
        // Intentionally assert the assignment's impossible post-crash expectation. Do not relaunch or mask it.
        assertTrue(wait.until(d -> !driver.currentActivity().endsWith("HomeScreenActivity")), "Crash must leave home before the demonstration assertion");
        boolean titleVisible = driver.findElements(AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"" + com.sdd.assessment.core.Config.get("mobile.title") + "\")"))
            .stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
        assertTrue(titleVisible, "Intentional assessment failure: home title is unavailable after the app crash");
    }
}
