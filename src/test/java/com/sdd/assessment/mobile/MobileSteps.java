package com.sdd.assessment.mobile;

import com.sdd.assessment.core.World;
import io.cucumber.java.en.*;

public final class MobileSteps {
    private final World world;
    private HomePage home;
    public MobileSteps(World world) { this.world = world; }
    @Given("Selendroid is freshly launched") public void launch() { home = new HomePage(world.mobile); home.verify(); }
    @Then("the home title and controls are correct") public void home() { home.verify(); }
    @When("I cancel the EN exit dialog and verify home") public void cancel() { home.cancelExit(); }
    @When("I submit the WebView form with Mercedes and verify its result and reset") public void webview() { home.webview(); new HelloWebViewPage(world.mobile).submitAndReset(); }
    @When("I verify registration defaults and register a new synthetic user") public void registration() {
        home.registration(); RegistrationPage registration = new RegistrationPage(world.mobile);
        registration.defaultsAndControls(); registration.registerAndVerify();
    }
    @When("I wait for progress to finish and verify registration controls") public void progress() { home.progress(); }
    @When("I display and verify the transient toast") public void toast() { home.toast(); }
    @When("I dismiss the popup and verify home") public void popup() { home.popup(); }
    @When("I trigger the unhandled exception button") public void crashButton() { home.crashButton(); }
    @When("I type test into the unhandled exception field") public void crashText() { home.crashText(); }
    @Then("the home title remains available after the deliberate crash") public void afterCrash() { home.assertStillHomeAfterCrash(); }
}
