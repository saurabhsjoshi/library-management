package com.fpts.api.model;

import com.fpts.api.model.validation.core.TestValidationsBuilder;

import java.io.Serializable;

/**
 * This is the main class that will be returned in a method annotated with
 * {@link com.fpts.api.annotation.PerformanceTest}. <br>
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
public record TestSpec<T extends Serializable>(Serializable testParameter, TestValidationsBuilder validationData) {

}
