package com.fpts.api.enums;

public enum SpecResponseFields {
    PARAMETER("parameter"),
    PARAMETER_SCHEMA("parameter-schema"),
    DESCRIPTION("description"),
    VALIDATION("validation");

    private final String fieldName;

    SpecResponseFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
