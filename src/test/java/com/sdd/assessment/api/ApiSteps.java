package com.sdd.assessment.api;

import com.sdd.assessment.core.Config;
import io.cucumber.java.en.*;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.Map;
import static org.testng.Assert.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public final class ApiSteps {
    private final ReqresClient client = new ReqresClient(Config.get("api.baseUrl"), System.getenv("REQRES_API_KEY"));
    private Response users;
    private Response created;
    private Map<String, String> body;
    @Given("I request users page 2") public void get() {
        users = client.users(2);
        assertEquals(users.statusCode(), 200, "GET /api/users?page=2: " + users.asString());
        Allure.addAttachment("GET response", "application/json", users.asString(), ".json");
    }
    @Then("user 10 has first name Byron") public void byron() {
        assertEquals(users.jsonPath().getInt("page"), 2);
        assertEquals(users.jsonPath().getString("data.find { it.id == 10 }.first_name"), "Byron");
    }
    @When("I create a user with a name derived from user 10") public void post() {
        body = ReqresClient.bodyFromUser(users, 10, Config.get("api.job"));
        created = client.createUser(body);
        Allure.addAttachment("POST response", "application/json", created.asString(), ".json");
    }
    @Then("the created user has status 201, a generated id and the required schema") public void validate() {
        assertEquals(created.statusCode(), 201, "POST /api/users: " + created.asString());
        created.then().body(matchesJsonSchemaInClasspath("schemas/create-user.json"));
        assertEquals(created.jsonPath().getString("name"), body.get("name"));
        assertEquals(created.jsonPath().getString("job"), body.get("job"));
        assertFalse(created.jsonPath().getString("id").isBlank());
        Instant.parse(created.jsonPath().getString("createdAt"));
    }
}
