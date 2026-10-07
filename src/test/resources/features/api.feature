@api
Feature: Reqres users API
  @API-01
  Scenario: Page two contains the expected user
    Given I request users page 2
    Then user 10 has first name Byron
  @API-02
  Scenario: User creation chains data from a fresh GET response
    Given I request users page 2
    Then user 10 has first name Byron
    When I create a user with a name derived from user 10
    Then the created user has status 201, a generated id and the required schema
