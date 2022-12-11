package org.joshi.raata.steps.service.inventory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.joshi.raata.steps.common.RestClient;
import org.joshi.raata.steps.common.SystemSteps;
import org.joshi.raata.steps.common.TestData;

import java.io.IOException;
import java.util.Map;

import static org.joshi.raata.steps.common.SystemSteps.getAuthPostReq;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventorySvcStepDefs {
    private static final String INVENTORY_API = "http://localhost/api/inventory/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        TestData.reset();
    }

    @When("I create a book with following information")
    public void iCreateABookWithFollowingInformation(DataTable dataTable) throws IOException {
        var data = dataTable.asMap(String.class, String.class);
        var post = getAuthPostReq(INVENTORY_API, data);
        try (var response = RestClient.getClient().execute(post)) {
            TestData.getInstance().statusCode = response.getCode();
        }
    }

    @And("I retrieve the book with book name {string}")
    public void iRetrieveTheBookWithBookNameTestBook(String bookname) throws IOException, ParseException {
        var get = new HttpGet(INVENTORY_API + bookname);
        try (var response = RestClient.getClient().execute(get)) {
            TestData.getInstance().data = EntityUtils.toString(response.getEntity());
            TestData.getInstance().statusCode = response.getCode();
        }
    }

    @Then("The fetched book should have the following information")
    public void theFetchedBookShouldHaveTheFollowingInformation(DataTable table) throws JsonProcessingException {
        var data = table.asMap(String.class, String.class);

        var typeRef = new TypeReference<Map<String, String>>() {
        };

        var resp = objectMapper.readValue(TestData.getInstance().data, typeRef);

        assertEquals(data, resp);
    }

    @And("I update the quantity of a book with name {string} as follows")
    public void iUpdateTheQuantityOfABookWithNameTestBookAsFollows(String bookname, DataTable dataTable) throws IOException {
        var req = SystemSteps.getAuthPostReq(INVENTORY_API + bookname + "/add",
                dataTable.asMap(String.class, String.class));
        try (var resp = RestClient.getClient().execute(req)) {
            assertEquals(HttpStatus.SC_OK, resp.getCode());
        }
    }

    @And("I delete a book with name {string}")
    public void iDeleteABookWithNameTest(String bookName) throws IOException {
        var req = new HttpDelete(INVENTORY_API + bookName);
        req.setHeader("username", TestData.getInstance().username);
        req.setHeader("password", TestData.getInstance().password);
        try (var resp = RestClient.getClient().execute(req)) {
            assertEquals(HttpStatus.SC_OK, resp.getCode());
        }
    }
}
