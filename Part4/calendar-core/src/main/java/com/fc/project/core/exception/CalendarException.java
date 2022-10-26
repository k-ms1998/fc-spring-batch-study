package com.fc.project.core.exception;

import lombok.Getter;


@Getter
public class CalendarException extends RuntimeException{
    private final ErrorCode errorCode;

    public CalendarException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
