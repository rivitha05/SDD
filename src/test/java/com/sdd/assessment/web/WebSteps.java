package com.sdd.assessment.web;

import com.sdd.assessment.core.World;
import io.cucumber.java.en.*;

public final class WebSteps {
    private final World world;
    private JQueryUiPage ui;
    public WebSteps(World world) { this.world = world; }
    @Given("I open the {string} jQuery UI demo") public void open(String menu) { ui = new JQueryUiPage(world.page); ui.open(menu); }
    @When("I drag the draggable into its target and verify acceptance") public void drop() { ui.drop(); }
    @When("I select only Items 1, 3 and 7") public void select() { ui.selectItems(); }
    @When("I configure and verify both illustrated rental forms") public void rental() { ui.rentalForms(); }
    @When("I choose and verify today's date") public void date() { ui.currentDate(); }
    @When("I enlarge the resizable box and verify its dimensions") public void resize() { ui.resize(); }
    @When("I sort all seven items descending and verify their order") public void sort() { ui.descendingSort(); }
    @When("I choose Go green and verify all three widget colors") public void green() { ui.green(); }
}
