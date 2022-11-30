package org.joshi.raata.steps.common;

import io.cucumber.java.en.Given;

public class SystemSteps {

    @Given("The library management system is running")
    public void systemIsRunning() {
        System.out.println("Hello World");
    }

}
