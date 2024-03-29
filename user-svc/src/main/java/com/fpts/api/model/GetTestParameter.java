package com.fpts.api.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a pair of values to be used as parameter in get
 * requests.<br>
 * A typical GET request will contain a list of parameter and value pairs.
 *
 * @author andre
 */
public class GetTestParameter implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -457401449569195463L;
    private final String parameter;
    private final String value;

    public GetTestParameter(String parameter, String value) {
        super();
        this.parameter = parameter;
        this.value = value;
    }

    public String getParameter() {
        return this.parameter;
    }

    public String getValue() {
        return this.value;
    }
}