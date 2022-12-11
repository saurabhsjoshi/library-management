package org.joshi.raata.steps.common;

/**
 * Static class that keeps track of test data related to a test suite.
 */
public class TestData {
    public String username;
    public String password;

    /**
     * Arbitrary data that can be stored as the test continues.
     */
    public String data;

    public int statusCode;

    private static TestData instance = null;

    private TestData() {
    }

    public static TestData getInstance() {
        if (instance == null) {
            instance = new TestData();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }
}
