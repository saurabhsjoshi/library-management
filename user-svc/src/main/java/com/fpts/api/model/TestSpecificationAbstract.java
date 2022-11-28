package com.fpts.api.model;

import com.fpts.api.model.validation.core.TestValidationsBuilder;

import java.io.Serializable;

public class TestSpecificationAbstract<T extends Serializable> {
    protected TestValidationsBuilder validationData;
}