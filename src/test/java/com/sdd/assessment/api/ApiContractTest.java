package com.sdd.assessment.api;

import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.annotations.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import static org.testng.Assert.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/** Local contract tests validate the real HTTP client and schema, not the external Reqres service. */
public final class ApiContractTest {
    private HttpServer server;
    private ReqresClient client;
    private final ObjectMapper json = new ObjectMapper();
    @BeforeClass public void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/users", exchange -> {
            byte[] output;
            int status;
            if (exchange.getRequestMethod().equals("GET")) {
                if (!"page=2".equals(exchange.getRequestURI().getQuery())) {
                    status = 400; output = "{\"error\":\"wrong page\"}".getBytes(StandardCharsets.UTF_8);
                } else {
                    status = 200; output = "{\"page\":2,\"data\":[{\"id\":10,\"first_name\":\"Byron\"}]}".getBytes(StandardCharsets.UTF_8);
                }
            } else if (exchange.getRequestMethod().equals("POST")) {
                @SuppressWarnings("unchecked") Map<String, Object> body = json.readValue(exchange.getRequestBody(), Map.class);
                body.put("id", "contract-123"); body.put("createdAt", Instant.now().toString());
                status = 201; output = json.writeValueAsBytes(body);
            } else { status = 405; output = new byte[0]; }
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, output.length);
            exchange.getResponseBody().write(output); exchange.close();
        });
        server.start();
        client = new ReqresClient("http://127.0.0.1:" + server.getAddress().getPort(), "");
    }
    @AfterClass(alwaysRun = true) public void stop() { if (server != null) server.stop(0); }
    @Test public void getToPostChainUsesTheFetchedNameAndValidatesContract() {
        Response users = client.users(2);
        assertEquals(users.statusCode(), 200);
        Map<String, String> body = ReqresClient.bodyFromUser(users, 10, "BA");
        Response result = client.createUser(body);
        assertEquals(result.statusCode(), 201);
        result.then().body(matchesJsonSchemaInClasspath("schemas/create-user.json"));
        assertEquals(result.jsonPath().getString("name"), "Byron");
        assertEquals(result.jsonPath().getString("job"), "BA");
        assertEquals(result.jsonPath().getString("id"), "contract-123");
        Instant.parse(result.jsonPath().getString("createdAt"));
    }
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*lacks user 999.*")
    public void absentSourceUserPreventsCreation() { ReqresClient.bodyFromUser(client.users(2), 999, "BA"); }
    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Job must not be blank.*")
    public void blankJobPreventsCreation() { ReqresClient.bodyFromUser(client.users(2), 10, " "); }
    @Test(expectedExceptions = AssertionError.class)
    public void schemaRejectsAnIncompleteCreateResponse() {
        client.users(2).then().body(matchesJsonSchemaInClasspath("schemas/create-user.json"));
    }
}
