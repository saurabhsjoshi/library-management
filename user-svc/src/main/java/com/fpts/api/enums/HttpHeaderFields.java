package com.fpts.api.enums;

public enum HttpHeaderFields {
    CONTENT_ENCODING("Content-Encoding"),
    ALLOW("Allow"),
    STATUS("Status"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    CREDENTIALS("Credentials");

    private final String field;

    HttpHeaderFields(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
