@web
Feature: jQuery UI assessment interactions
  @WEB-01
  Scenario: Droppable accepts the dragged component
    Given I open the "Droppable" jQuery UI demo
    When I drag the draggable into its target and verify acceptance
  @WEB-02
  Scenario: Selectable supports noncontiguous selections
    Given I open the "Selectable" jQuery UI demo
    When I select only Items 1, 3 and 7
  @WEB-03
  Scenario: Both rental controlgroups match the assessment image
    Given I open the "Controlgroup" jQuery UI demo
    When I configure and verify both illustrated rental forms
  @WEB-04
  Scenario: Datepicker selects the current date in the configured timezone
    Given I open the "Datepicker" jQuery UI demo
    When I choose and verify today's date
  @WEB-05
  Scenario: Resizable changes actual rendered dimensions
    Given I open the "Resizable" jQuery UI demo
    When I enlarge the resizable box and verify its dimensions
  @WEB-06
  Scenario: Sortable reverses the complete ascending list
    Given I open the "Sortable" jQuery UI demo
    When I sort all seven items descending and verify their order
  @WEB-07
  Scenario: Widget Factory applies the defined green RGB value
    Given I open the "Widget Factory" jQuery UI demo
    When I choose Go green and verify all three widget colors
