package org.joshi.raata;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Test runner.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/joshi/raata")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.joshi.raata")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources")
public class AcceptanceTestSuite {
}
