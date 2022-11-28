package com.fpts.api.model;

import com.fpts.api.model.validation.core.TestValidationsBuilder;

import java.io.Serializable;

/**
 * This is the main class that will be returned in a method annotated with
 * {@link PerformanceTest}. <br>
 * This class holds a parameter of type {@link Serializable} which will
 * represent the parameter to be used in the test request. <br>
 * This class may also contain several values to be used to validate the
 * response of the service for a test request. {@link ValidationData} <br>
 * The validation data can be the body content of the response or header
 * parameters.
 *
 * @param <T>
 * @author andre
 */
public class TestSpec<T extends Serializable> {

    private final Serializable testParameter;

    private final TestValidationsBuilder validationData;

    public TestSpec(Serializable testParameter, TestValidationsBuilder validationData) {
        super();
        this.testParameter = testParameter;
        this.validationData = validationData;
    }

    public Serializable getTestParameter() {
        return this.testParameter;
    }

    public TestValidationsBuilder getValidationData() {
        return this.validationData;
    }

}
