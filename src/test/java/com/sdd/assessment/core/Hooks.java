package com.sdd.assessment.core;

import io.cucumber.java.*;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import java.io.ByteArrayInputStream;
import java.nio.file.*;

public final class Hooks {
    private static final Logger LOG = LoggerFactory.getLogger(Hooks.class);
    private final World world;
    public Hooks(World world) { this.world = world; }
    @Before("@web") public void web() { world.startWeb(); }
    @Before("@mobile") public void mobile() throws Exception { world.startMobile(); }
    @Before public void logStart(Scenario scenario) { LOG.info("Starting {}", scenario.getName()); }
    @After public void finish(Scenario scenario) throws Exception {
        try {
            Path dir = Path.of("target", "evidence"); Files.createDirectories(dir);
            String id = scenario.getId().replaceAll("[^A-Za-z0-9_-]", "_");
            try {
                byte[] screenshot = world.page != null ? world.page.screenshot(new Page.ScreenshotOptions().setFullPage(true))
                    : world.mobile != null ? world.mobile.getScreenshotAs(OutputType.BYTES) : null;
                if (screenshot != null) {
                    Files.write(dir.resolve(id + ".png"), screenshot);
                    scenario.attach(screenshot, "image/png", "Final screen");
                }
                if (world.mobile != null && scenario.isFailed()) {
                    Allure.addAttachment("Native page source", "application/xml", world.mobile.getPageSource(), ".xml");
                    if (scenario.getSourceTagNames().contains("@expected-failure")) {
                        String crashLog = world.mobile.manage().logs().get("logcat").getAll().stream()
                            .map(Object::toString).filter(line -> line.contains("AndroidRuntime"))
                            .collect(java.util.stream.Collectors.joining("\n"));
                        Allure.addAttachment("Android crash log", "text/plain", crashLog, ".txt");
                    }
                }
            } catch (Exception e) { LOG.warn("Evidence capture failed for {}: {}", scenario.getName(), e.getMessage()); }
            if (world.context != null) {
                Path trace = dir.resolve(id + ".zip");
                world.context.tracing().stop(new Tracing.StopOptions().setPath(scenario.isFailed() ? trace : null));
                if (scenario.isFailed()) {
                    try (var input = Files.newInputStream(trace)) { Allure.addAttachment("Playwright trace", "application/zip", input, ".zip"); }
                }
            }
        } finally { world.close(); LOG.info("Finished {}: {}", scenario.getName(), scenario.getStatus()); }
    }
}
