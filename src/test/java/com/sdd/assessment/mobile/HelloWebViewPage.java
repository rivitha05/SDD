package com.sdd.assessment.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.testng.Assert.*;

/** The supplied legacy APK does not enable WebView debugging. Exercise its exposed accessibility controls. */
public final class HelloWebViewPage extends NativePage {
    private static final String NAME = MobileUser.load().name();
    public HelloWebViewPage(AndroidDriver driver) { super(driver); }
    private org.openqa.selenium.By text(String value) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + value + "\")");
    }
    private void tap(WebElement element) { tap(element.getRect()); }
    private void tap(org.openqa.selenium.Rectangle rect) {
        var finger = new org.openqa.selenium.interactions.PointerInput(
            org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "finger");
        var tap = new org.openqa.selenium.interactions.Sequence(finger, 0)
            .addAction(finger.createPointerMove(java.time.Duration.ZERO,
                org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                rect.x + rect.width / 2, rect.y + rect.height / 2))
            .addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new org.openqa.selenium.interactions.Pause(finger, java.time.Duration.ofMillis(100)))
            .addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.List.of(tap));
    }
    public void submitAndReset() {
        driver.setSetting("enableMultiWindows", true);
        title();
        assertTrue(driver.currentActivity().endsWith("WebViewActivity"));
        visible(text("Hello, can you please tell me your name?"));
        WebElement input = visible(AppiumBy.className("android.widget.EditText")); input.clear(); input.sendKeys(NAME);
        if (driver.isKeyboardShown()) driver.hideKeyboard();
        tap(visible(text("Volvo")));
        // UiSelector searches only the active window; XPath includes the separate dropdown.
        tap(visible(AppiumBy.xpath("//*[@text='Mercedes']")));
        tap(visible(text("Send me your name!")));
        visible(text("This is my way of saying hello"));
        visible(text("\"" + NAME + "\""));
        visible(text("\"mercedes\""));
        // The result is taller than this legacy wrap-content WebView. Scroll its viewport,
        // not the surrounding activity, to bring the bottom link fully into view.
        WebElement webview = visible(AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.webkit.WebView\").scrollable(true)"));
        var area = webview.getRect();
        var finger = new org.openqa.selenium.interactions.PointerInput(
            org.openqa.selenium.interactions.PointerInput.Kind.TOUCH, "scroll-finger");
        var scroll = new org.openqa.selenium.interactions.Sequence(finger, 0)
            .addAction(finger.createPointerMove(java.time.Duration.ZERO,
                org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                area.x + area.width / 2, area.y + area.height * 9 / 10))
            .addAction(finger.createPointerDown(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()))
            .addAction(new org.openqa.selenium.interactions.Pause(finger, java.time.Duration.ofMillis(100)))
            .addAction(finger.createPointerMove(java.time.Duration.ofMillis(500),
                org.openqa.selenium.interactions.PointerInput.Origin.viewport(),
                area.x + area.width / 2, area.y + area.height / 10))
            .addAction(finger.createPointerUp(org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(java.util.List.of(scroll));
        WebElement restart = wait.until(d -> {
            WebElement link = d.findElement(text("here"));
            return link.getRect().height > 1 ? link : null;
        });
        tap(restart);
        visible(text("Hello, can you please tell me your name?"));
        assertEquals(visible(text("Volvo")).getText(), "Volvo", "Reset restores default preferred car");
    }
}
