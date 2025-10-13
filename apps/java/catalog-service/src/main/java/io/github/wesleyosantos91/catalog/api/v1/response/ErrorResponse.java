package io.github.wesleyosantos91.catalog.api.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorResponse(

        String field,
        String message,
        Object rejectedValue,
        String errorCode

) {
    public ErrorResponse(String field, String message) {
        this(field, message, null, null);
    }

    public ErrorResponse(String field, String message, Object rejectedValue) {
        this(field, message, rejectedValue, null);
    }
}