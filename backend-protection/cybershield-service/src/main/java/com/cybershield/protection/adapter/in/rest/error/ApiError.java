package com.cybershield.protection.adapter.in.rest.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(Integer statusCode, String httpStatus, String message, List<String> details) {

    public ApiError {
        if (statusCode == null) throw new IllegalArgumentException("statusCode cannot be null");
        if (httpStatus == null) throw new IllegalArgumentException("httpStatus cannot be null");
        if (message == null) throw new IllegalArgumentException("message cannot be null");
    }

    public ApiError(Integer statusCode, String httpStatus, String message) {
        this(statusCode, httpStatus, message, null);
    }
}