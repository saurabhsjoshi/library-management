package org.joshi.user;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite that uses FPTS (Framework for Performance Test Specification for Microservices)
 * Source for framework: <a href="https://github.com/asdcamargo/fpts">FPTS</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserSvcFptsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private Stream<Arguments> fptsGenerator() {
        var req = new HttpEntity<>("");
        var resp = restTemplate.exchange("http://localhost:" + port + "/api/users/",
                HttpMethod.OPTIONS, req, String.class);

        var respBody = resp.getBody();

        if (respBody == null) {
            fail("Spec not received via OPTIONS call for users API");
            return Stream.empty();
        }

        var obj = JsonParser.parseString(respBody).getAsJsonObject();

        var stream = Stream.of(Pair.of(HttpMethod.POST, obj.remove("POST").getAsJsonObject()),
                        Pair.of(HttpMethod.GET, obj.remove("GET").getAsJsonObject()),
                        Pair.of(HttpMethod.PUT, obj.remove("PUT").getAsJsonObject()),
                        Pair.of(HttpMethod.DELETE, obj.remove("DELETE").getAsJsonObject()))
                .map(pair -> Arguments.of(pair.getFirst(), pair.getSecond()));

        assertTrue(obj.asMap().isEmpty());

        return stream;
    }

    @ParameterizedTest
    @MethodSource("fptsGenerator")
    void testRunner(HttpMethod method, JsonObject obj) {
        String userApi = "http://localhost:" + port + "/api/users/";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var validationBody = JsonParser.parseString(obj.get("validation").getAsString()).getAsJsonObject();

        if (validationBody.has("jsonrepresentation")) {
            validationBody = JsonParser.parseString(validationBody.get("jsonrepresentation").getAsString()).getAsJsonObject();
        }

        var expectedStatus = validationBody.get("HEADER")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject()
                .get("Status").getAsString();

        // Create
        if (method == HttpMethod.POST) {
            var payload = JsonParser.parseString(obj.get("parameter").getAsString()).toString();

            var req = new HttpEntity<>(payload, headers);
            var resp = restTemplate.postForEntity(userApi, req, String.class);

            assertEquals(expectedStatus, Integer.toString(resp.getStatusCode().value()));
            return;
        }

        // Read
        if (method == HttpMethod.GET) {
            var parameter = JsonParser.parseString(obj.get("parameter").getAsString())
                    .getAsJsonObject().
                    get("value")
                    .getAsString();

            var resp = restTemplate.getForEntity(userApi + parameter, String.class);

            assertEquals(expectedStatus, Integer.toString(resp.getStatusCode().value()));
            return;
        }

        // Update
        if (method == HttpMethod.PUT) {
            var payload = JsonParser.parseString(obj.get("parameter").getAsString()).toString();
            var req = new HttpEntity<>(payload, headers);
            var resp = restTemplate.exchange(userApi, method, req, String.class);
            assertEquals(expectedStatus, Integer.toString(resp.getStatusCode().value()));
            return;
        }

        // Delete
        if (method == HttpMethod.DELETE) {
            var parameter = JsonParser.parseString(obj.get("parameter").getAsString())
                    .getAsJsonObject().
                    get("value")
                    .getAsString();

            var req = new HttpEntity<>(headers);
            var resp = restTemplate.exchange(userApi + parameter, method, req, String.class);

            assertEquals(expectedStatus, Integer.toString(resp.getStatusCode().value()));
        }
    }
}
