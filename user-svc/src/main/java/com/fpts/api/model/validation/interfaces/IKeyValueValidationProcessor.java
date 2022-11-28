package com.fpts.api.model.validation.interfaces;


import com.fpts.api.enums.ValidationType;

/**
 * Basic interface that sets a class as a validator processor for key:value
 * pairs of data.<br>
 *
 * @author andre
 */
public interface IKeyValueValidationProcessor extends IValidation {

    public void putContent(String field, String data);

    public ValidationType getValidationType();

}
