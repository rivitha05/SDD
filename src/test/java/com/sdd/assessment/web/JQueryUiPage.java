package com.sdd.assessment.web;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import com.sdd.assessment.core.Config;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.*;

/** Owns iframe boundaries and stable demo IDs; assertions describe observable widget behavior. */
public final class JQueryUiPage {
    private final Page page;
    private final FrameLocator demo;
    public JQueryUiPage(Page page) { this.page = page; demo = page.frameLocator("iframe.demo-frame"); }
    public void open(String menu) {
        page.navigate(Config.get("web.baseUrl"));
        page.locator("#sidebar").getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(menu).setExact(true)).click();
        assertThat(page.locator("iframe.demo-frame")).isVisible();
    }
    public void drop() {
        demo.locator("#draggable").dragTo(demo.locator("#droppable"));
        assertThat(demo.locator("#droppable")).hasText("Dropped!");
        assertThat(demo.locator("#droppable")).hasClass(java.util.regex.Pattern.compile(".*ui-state-highlight.*"));
    }
    public void selectItems() {
        Locator items = demo.locator("#selectable li");
        for (int index : new int[] {0, 2, 6}) {
            items.nth(index).click(new Locator.ClickOptions().setModifiers(List.of(KeyboardModifier.CONTROL)));
        }
        assertThat(demo.locator("#selectable .ui-selected")).hasText(new String[] {"Item 1", "Item 3", "Item 7"});
    }
    public void rentalForms() {
        configureRental(".controlgroup", "SUV", "transmission-automatic", "insurance", "horizontal-spinner", "2");
        configureRental(".controlgroup-vertical", "Truck", "transmission-standard-v", "insurance-v", "vertical-spinner", "1");
    }
    private void configureRental(String root, String car, String transmission, String insurance, String spinner, String count) {
        Locator group = demo.locator(root);
        group.locator(".ui-selectmenu-button").click();
        demo.getByRole(AriaRole.OPTION, new FrameLocator.GetByRoleOptions().setName(car).setExact(true)).click();
        group.locator("label[for='" + transmission + "']").click();
        if (!group.locator("#" + insurance).isChecked()) group.locator("label[for='" + insurance + "']").click();
        group.locator("#" + spinner).fill(count);
        assertEquals(group.locator("select").inputValue(), car, "Selected car");
        assertThat(group.locator("#" + transmission)).isChecked();
        assertThat(group.locator("#" + insurance)).isChecked();
        assertThat(group.locator("#" + spinner)).hasValue(count);
        group.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Book Now!").setExact(true)).click();
        // This demo has no booking backend. Verify the selected state remains after the action.
        assertEquals(group.locator("select").inputValue(), car);
        assertThat(group.locator("#" + spinner)).hasValue(count);
    }
    public void currentDate() {
        LocalDate today = LocalDate.now(ZoneId.of(Config.get("web.timezone")));
        demo.locator("#datepicker").click();
        demo.locator(".ui-datepicker-today a").click();
        assertThat(demo.locator("#datepicker")).hasValue(today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }
    public void resize() {
        Locator box = demo.locator("#resizable");
        BoundingBox before = box.boundingBox();
        BoundingBox handle = demo.locator("#resizable .ui-resizable-se").boundingBox();
        assertNotNull(before); assertNotNull(handle);
        page.mouse().move(handle.x + handle.width / 2, handle.y + handle.height / 2);
        page.mouse().down();
        page.mouse().move(handle.x + 100, handle.y + 80, new Mouse.MoveOptions().setSteps(15));
        page.mouse().up();
        BoundingBox after = box.boundingBox();
        assertNotNull(after);
        assertTrue(after.width > before.width + 50, "Width must increase meaningfully");
        assertTrue(after.height > before.height + 40, "Height must increase meaningfully");
    }
    public void descendingSort() {
        Locator items = demo.locator("#sortable li");
        assertThat(items).hasText(new String[] {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7"});
        // Drag each remaining ascending item above the first. Real mouse events exercise jQuery UI sorting.
        for (int n = 2; n <= 7; n++) {
            Locator source = items.filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^Item " + n + "$")));
            BoundingBox from = source.boundingBox();
            BoundingBox to = items.first().boundingBox();
            assertNotNull(from); assertNotNull(to);
            page.mouse().move(from.x + from.width / 2, from.y + from.height / 2);
            page.mouse().down();
            page.mouse().move(to.x + to.width / 2, to.y + 2, new Mouse.MoveOptions().setSteps(20));
            page.mouse().up();
            assertThat(items.first()).hasText("Item " + n);
        }
        assertThat(items).hasText(new String[] {"Item 7", "Item 6", "Item 5", "Item 4", "Item 3", "Item 2", "Item 1"});
    }
    public void green() {
        demo.locator("#green").click();
        for (int n = 1; n <= 3; n++) assertThat(demo.locator("#my-widget" + n)).hasCSS("background-color", "rgb(64, 250, 8)");
    }
}
