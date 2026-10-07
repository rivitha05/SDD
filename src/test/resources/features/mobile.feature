@mobile
Feature: Selendroid Android assessment
  @MOB-01
  Scenario: Home title and controls
    Given Selendroid is freshly launched
    Then the home title and controls are correct
  @MOB-02
  Scenario: Cancel the EN exit dialog
    Given Selendroid is freshly launched
    When I cancel the EN exit dialog and verify home
  @MOB-03
  Scenario: WebView Mercedes submission and Volvo reset
    Given Selendroid is freshly launched
    When I submit the WebView form with Mercedes and verify its result and reset
  @MOB-04
  Scenario: Registration defaults, confirmation and return home
    Given Selendroid is freshly launched
    When I verify registration defaults and register a new synthetic user
  @MOB-05
  Scenario: Progress completion navigates to registration
    Given Selendroid is freshly launched
    When I wait for progress to finish and verify registration controls
  @MOB-06
  Scenario: Transient toast contains the expected message
    Given Selendroid is freshly launched
    When I display and verify the transient toast
  @MOB-07
  Scenario: Dismiss popup
    Given Selendroid is freshly launched
    When I dismiss the popup and verify home
  @expected-failure @MOB-08
  Scenario: Deliberate button crash fails the home expectation
    Given Selendroid is freshly launched
    When I trigger the unhandled exception button
    Then the home title remains available after the deliberate crash
  @expected-failure @MOB-09
  Scenario: Deliberate text crash fails the home expectation
    Given Selendroid is freshly launched
    When I type test into the unhandled exception field
    Then the home title remains available after the deliberate crash
