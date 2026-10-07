package com.sdd.assessment.mobile;

import com.sdd.assessment.core.Config;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import static org.testng.Assert.*;

public abstract class NativePage {
    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected NativePage(AndroidDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(Config.positiveInt("mobile.timeoutSeconds")));
    }
    protected By id(String value) { return AppiumBy.id(Config.get("mobile.package") + ":id/" + value); }
    protected WebElement visible(By by) { return wait.until(ExpectedConditions.visibilityOfElementLocated(by)); }
    protected WebElement field(String name) {
        By by = id(name);
        if (driver.findElements(by).stream().noneMatch(WebElement::isDisplayed)) {
            driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().resourceId(\"" + Config.get("mobile.package") + ":id/" + name + "\"))"));
        }
        return visible(by);
    }
    protected void type(String name, String value) {
        WebElement input = field(name); input.clear(); input.sendKeys(value);
        if (driver.isKeyboardShown()) driver.hideKeyboard();
    }
    public void title() { assertTrue(visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + Config.get("mobile.title") + "\")")).isDisplayed(), "Application title"); }
}
