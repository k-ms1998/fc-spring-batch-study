package com.fc.project.core.exception;

public enum ErrorCode {
    PASSWORD_NOT_MATCH("Password does not match."),
    USER_ALREADY_EXISTS("User already exists."),
    USER_NOT_FOUND("User not found"),
    VALIDATION_FAIL("Invalid."),
    BAD_REQUEST("Bad Request. Try Again."),
    EVENT_CREATE_OVERLAPPED("Event overlaps with another event.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
