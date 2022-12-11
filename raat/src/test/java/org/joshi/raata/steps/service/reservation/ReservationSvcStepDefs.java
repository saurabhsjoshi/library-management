package org.joshi.raata.steps.service.reservation;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.apache.hc.core5.http.HttpStatus;
import org.joshi.raata.steps.common.RestClient;
import org.joshi.raata.steps.common.SystemSteps;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationSvcStepDefs {
    private static final String RESERVATION_URI = "http://localhost/reservations";

    @When("I reserve a book with following details")
    public void iReserveABookWithFollowingDetails(DataTable dataTable) throws IOException {
        var req = SystemSteps.getAuthPostReq(RESERVATION_URI, dataTable.asMap(String.class, String.class));
        try (var resp = RestClient.getClient().execute(req)) {
            assertEquals(HttpStatus.SC_OK, resp.getCode());
        }
    }
}
