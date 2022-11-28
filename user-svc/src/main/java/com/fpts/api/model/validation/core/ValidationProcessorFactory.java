package com.fpts.api.model.validation.core;


import com.fpts.api.enums.ValidationType;
import com.fpts.api.model.validation.interfaces.IKeyValueValidationProcessor;

/**
 * Factory to instantiate ValidationProcessors based on {@link ValidationType}
 *
 * @author andre
 */
public class ValidationProcessorFactory {

    public static IKeyValueValidationProcessor getProcessorForType(ValidationType type) {
        if (type == ValidationType.HEADER) {
            return new HeaderValidationProcessor();
        }
        return new BodyValidationProcessor();
    }

}
