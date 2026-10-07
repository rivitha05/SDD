package com.sdd.assessment.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import static org.testng.Assert.*;

public final class RegistrationPage extends NativePage {
    private static final MobileUser DATA = MobileUser.load();
    private static final String USERNAME = DATA.username();
    private static final String NAME = DATA.name();
    private static final String EMAIL = DATA.email();
    private static final String PASSWORD = DATA.password();
    private static final String LANGUAGE = DATA.language();
    public RegistrationPage(AndroidDriver driver) { super(driver); }
    public void defaultsAndControls() {
        title();
        assertTrue(driver.currentActivity().endsWith("RegisterUserActivity"));
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Welcome to register a new User\")"));
        for (String name : new String[] {"inputUsername", "inputEmail", "inputPassword", "inputName", "input_preferedProgrammingLanguage", "input_adds", "btnRegisterUser"}) assertTrue(field(name).isEnabled(), "Registration control " + name);
        assertEquals(field("inputName").getText(), "Mr. Burns");
        assertEquals(field("input_preferedProgrammingLanguage").findElement(AppiumBy.className("android.widget.TextView")).getText(), "Ruby");
    }
    public void registerAndVerify() {
        type("inputUsername", USERNAME); type("inputEmail", EMAIL); type("inputPassword", PASSWORD); type("inputName", NAME);
        field("input_preferedProgrammingLanguage").click();
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"" + LANGUAGE + "\")")).click();
        if (!"true".equals(field("input_adds").getAttribute("checked"))) field("input_adds").click();
        field("btnRegisterUser").click();
        visible(AppiumBy.androidUIAutomator("new UiSelector().text(\"Verify user\")"));
        assertEquals(visible(id("label_name_data")).getText(), NAME);
        assertEquals(visible(id("label_username_data")).getText(), USERNAME);
        assertEquals(visible(id("label_email_data")).getText(), EMAIL);
        assertEquals(visible(id("label_password_data")).getText(), PASSWORD);
        assertEquals(visible(id("label_preferedProgrammingLanguage_data")).getText(), LANGUAGE);
        assertEquals(visible(id("label_acceptAdds_data")).getText(), "true");
        visible(id("buttonRegisterUser")).click();
        new HomePage(driver).verify();
    }
}
