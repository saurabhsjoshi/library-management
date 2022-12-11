package org.joshi.raata.steps.common;

import io.cucumber.java.en.And;

public class AuthStepDefs {

    @And("I am logged in as the {string} user")
    public void iAmLoggedInAsTheAdminUser(String username) {
        TestData.getInstance().username = username;
        TestData.getInstance().password = username;
    }
}
