package org.joshi.raata.steps.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.joshi.raata.steps.common.RestClient;
import org.joshi.raata.steps.common.SystemSteps;
import org.joshi.raata.steps.common.TestData;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSvcStepDefs {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_API = "http://localhost/api/users/";

    @When("Admin user creates a user with following details")
    public void adminCreatesUser(DataTable dataTable) throws IOException {
        var client = RestClient.getClient();
        var post = SystemSteps.getAuthPostReq(USERS_API, dataTable.asMap(String.class, String.class));
        try (var response = client.execute(post)) {
            assertEquals(HttpStatus.SC_CREATED, response.getCode());
        }
    }

    @And("Admin fetches the user with username {string}")
    public void adminFetchesUser(String username) throws IOException, ParseException {
        var client = RestClient.getClient();
        try (var response = client.execute(new HttpGet(USERS_API + username))) {
            assertEquals(HttpStatus.SC_OK, response.getCode());
            TestData.getInstance().data = EntityUtils.toString(response.getEntity());
        }
    }

    @Then("The fetched user should have username the following")
    public void validateFetchedUser(DataTable dataTable) throws JsonProcessingException {
        var data = dataTable.asMap(String.class, String.class);

        var typeRef = new TypeReference<Map<String, String>>() {
        };

        var resp = objectMapper.readValue(TestData.getInstance().data, typeRef);

        assertEquals(data, resp);
    }
}
