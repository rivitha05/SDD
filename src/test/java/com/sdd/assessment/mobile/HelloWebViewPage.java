package com.sdd.assessment.mobile;

import com.sdd.assessment.core.Config;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import static org.testng.Assert.*;

/** Uses native activity checks and WebView DOM controls for the embedded Hello form. */
public final class HelloWebViewPage extends NativePage {
    private static final String QUESTION = "Hello, can you please tell me your name?";
    private final String name = MobileUser.load().name();
    public HelloWebViewPage(AndroidDriver driver) { super(driver); }

    public void submitAndReset() {
        title();
        assertTrue(driver.currentActivity().endsWith("WebViewActivity"), "WebView activity");
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + QUESTION + "\")"));
        String context = wait.until(d -> driver.getContextHandles().stream()
            .filter(handle -> handle.equals("WEBVIEW_" + Config.get("mobile.package")))
            .findFirst().orElse(null));
        try {
            driver.context(context);
            WebElement input = visible(By.id("name_input"));
            String formUrl = driver.getCurrentUrl();
            input.clear();
            input.sendKeys(name);
            Select car = new Select(visible(By.name("car")));
            car.selectByValue("mercedes");
            assertEquals(car.getFirstSelectedOption().getText(), "Mercedes");
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='submit']"))).click();
            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "\"" + name + "\""));
            assertEquals(visible(By.tagName("h1")).getText(), "This is my way of saying hello");
            var result = visible(By.tagName("body")).getText().lines().map(String::trim).toList();
            assertTrue(result.contains("\"" + name + "\""), "Submitted name");
            assertTrue(result.contains("\"mercedes\""), "Submitted preferred car");
            // Native accessibility bounds are stale after this legacy WebView changes height.
            // A DOM click follows the actual reset link, without coordinate guessing or navigation shortcuts.
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("here"))).click();
            wait.until(ExpectedConditions.urlToBe(formUrl));
            assertEquals(visible(By.id("name_input")).getAttribute("value"), "Enter your name here!", "Reset restores name default");
            assertEquals(new Select(visible(By.name("car"))).getFirstSelectedOption().getText(), "Volvo", "Reset restores default preferred car");
        } finally {
            driver.context("NATIVE_APP");
        }
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + QUESTION + "\")"));
        title();
        assertTrue(driver.currentActivity().endsWith("WebViewActivity"), "Reset remains in WebView activity");
    }
}
