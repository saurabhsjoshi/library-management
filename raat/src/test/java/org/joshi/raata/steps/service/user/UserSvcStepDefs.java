package org.joshi.raata.steps.service.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.joshi.raata.steps.common.RestClient;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.List;

public class UserSvcStepDefs {

    private static final String USERS_API = "http://localhost/api/users/";

    @When("Admin user creates a user with username {string}, name {string} and password {string}")
    public void adminCreatesUser(String username, String name, String password) throws IOException {
        var client = RestClient.getClient();
        var post = new HttpPost(USERS_API);
        String createUserReq = """
                {
                    "username" : "%s",
                    "password" : "%s",
                    "displayName" : "%s"
                }
                """;
        post.setEntity(new StringEntity(String.format(createUserReq, username, password, name)));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        var response = client.execute(post);
        Assertions.assertEquals(HttpStatus.SC_CREATED, response.getCode());
    }

    @And("Admin fetches the user with username {string}")
    public void adminFetchesUser(String username) throws IOException {
        var client = RestClient.getClient();
        var response = client.execute(new HttpGet(USERS_API + username));
        Assertions.assertEquals(HttpStatus.SC_OK, response.getCode());
    }

    @Then("The fetched user should have username the following")
    public void validateFetchedUser(List<String> userInfo) {
        System.out.println("User has following info:");
        for (var u : userInfo) {
            System.out.println(u);
        }
    }
}
