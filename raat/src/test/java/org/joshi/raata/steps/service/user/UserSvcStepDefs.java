package org.joshi.raata.steps.service.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

public class UserSvcStepDefs {

    @When("Admin user creates a user with username {string}, name {string} and password {string}")
    public void adminCreatesUser(String username, String name, String password) {
        System.out.println("Hello world " + username + " " + name + " " + password);
    }

    @And("Admin fetches the user with username {string}")
    public void adminFetchesUser(String username) {
        System.out.println("Fetched user with name " + username);
    }

    @Then("The fetched user should have username the following")
    public void validateFetchedUser(List<String> userInfo) {
        System.out.println("User has following info:");
        for (var u : userInfo) {
            System.out.println(u);
        }
    }
}
