package com.investment.controller.exceptionhandling;

import lombok.Getter;

import java.net.URI;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiProblemType {
    REQUEST_VALIDATION_VIOLATION_ERROR(
            URI.create("/validation-failed/constraint-violation"),
            "Constraint Violation",
                    "Constraint violation(s) occurred during request validation. Check violation(s) for more details",
    HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(
            URI.create("/internal-server-error"),
            "Unexpected internal server error",
                    "Unexpected internal server error occurred. Please contact service administrator for more details.",
    HttpStatus.INTERNAL_SERVER_ERROR),
    TOO_MANY_REQUESTS(
            URI.create("/too-many-requests"),
            "Too many requests",
                    "Number of requests exceeds 10 within one minute or the number of requests exceeds 5 within 20 seconds.",
    HttpStatus.TOO_MANY_REQUESTS),
    RESOURCE_NOT_FOUND_ERROR(
            URI.create("/resource-not-found"), "Resource not found", "Resource not found", HttpStatus.NOT_FOUND),
    BAD_REQUEST_ERROR(URI.create("/bad-request"), "Bad Request", "Request validation failed.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED_ERROR(
            URI.create("/method-not-allowed"),
            "Method Not Allowed",
                    "Method Not Allowed",
    HttpStatus.METHOD_NOT_ALLOWED),
    PO_ALREADY_EXISTS(
            URI.create("/bad-request"),
            "Bad Request",
                    "Purchase order already exists.", HttpStatus.BAD_REQUEST
    );

    private final URI uri;
    private final String title;
    private final String message;
    private final HttpStatus status;

    ApiProblemType(URI uri, String title, String message, HttpStatus status) {
        this.uri = uri;
        this.title = title;
        this.message = message;
        this.status = status;
    }
}
