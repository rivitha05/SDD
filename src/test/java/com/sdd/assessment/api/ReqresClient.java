package com.sdd.assessment.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import java.util.Map;
import static io.restassured.RestAssured.given;

/** No global RestAssured state and no request/header logging that could expose an API key. */
public final class ReqresClient {
    private final RequestSpecification spec;
    public ReqresClient(String baseUrl, String apiKey) {
        RequestSpecBuilder builder = new RequestSpecBuilder().setBaseUri(baseUrl).setContentType(ContentType.JSON).addHeader("User-Agent", "SDD-Assessment/1.0")
            .setConfig(io.restassured.config.RestAssuredConfig.config().httpClient(
                io.restassured.config.HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", 20000).setParam("http.socket.timeout", 20000)));
        if (apiKey != null && !apiKey.isBlank()) builder.addHeader("x-api-key", apiKey);
        // Route public HTTPS through the supplied proxy when present; never proxy local contract tests.
        String proxy = System.getenv("HTTPS_PROXY");
        if (baseUrl.startsWith("https://") && proxy != null && !proxy.isBlank()) {
            java.net.URI uri = java.net.URI.create(proxy);
            builder.setProxy(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort(), uri.getScheme());
        }
        spec = builder.build();
    }
    public Response users(int page) { return given().spec(spec).queryParam("page", page).get("/api/users"); }
    public Response createUser(Map<String, String> body) { return given().spec(spec).body(body).post("/api/users"); }
    public static Map<String, String> bodyFromUser(Response response, int id, String job) {
        String name = response.jsonPath().getString("data.find { it.id == " + id + " }.first_name");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("GET response lacks user " + id);
        if (job == null || job.isBlank()) throw new IllegalArgumentException("Job must not be blank");
        return Map.of("name", name, "job", job);
    }
}
