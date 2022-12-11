package org.joshi.raata.steps.service.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
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
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReservationSvcStepDefs {
    private static final String RESERVATION_URI = "http://localhost/api/reservations/";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private void deleteReservation() throws IOException {
        var delete = new HttpDelete(RESERVATION_URI + "1234");
        delete.setHeader("username", TestData.getInstance().username);
        delete.setHeader("password", TestData.getInstance().password);

        try (var resp = RestClient.getClient().execute(delete)) {
        } catch (Exception ignore) {
            // ignore
        }
    }

    @After(value = "@ValidateBorrow")
    public void cleanup() throws Throwable {
        deleteReservation();
    }

    @When("I reserve a book with following details")
    public void iReserveABookWithFollowingDetails(DataTable dataTable) throws IOException {
        var req = SystemSteps.getAuthPostReq(RESERVATION_URI, dataTable.asMap(String.class, String.class));
        try (var resp = RestClient.getClient().execute(req)) {
            TestData.getInstance().statusCode = resp.getCode();
        }
    }

    @And("I retrieve the reservation with id {string}")
    public void iRetrieveTheReservationWithId(String reservationId) throws IOException, ParseException {
        var get = new HttpGet(RESERVATION_URI + reservationId);
        get.setHeader("username", TestData.getInstance().username);
        get.setHeader("password", TestData.getInstance().password);
        try (var response = RestClient.getClient().execute(get)) {
            TestData.getInstance().data = EntityUtils.toString(response.getEntity());
            TestData.getInstance().statusCode = response.getCode();
        }
    }

    @Then("The fetched reservation should have following details")
    public void theFetchedReservationShouldHaveFollowingDetails(DataTable dataTable) throws JsonProcessingException {
        var data = dataTable.asMap(String.class, String.class);
        var typeRef = new TypeReference<Map<String, String>>() {
        };

        var resp = objectMapper.readValue(TestData.getInstance().data, typeRef);

        for (var entry : data.entrySet()) {
            var attr = resp.get(entry.getKey());
            assertNotNull(attr);
            assertEquals(entry.getValue(), attr);
        }
    }
}
