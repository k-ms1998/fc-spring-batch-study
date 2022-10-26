package com.fc.project.api.exception;

import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception Handler:
 * Exception 이 발생했을때, exception 을 던지지 않고, Response 로  에러코드를 반환하는 방식으로 처리
 * Because, exception 을 던지는 방식은 overhead 가 많기 때문에
 */
//@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Calendar Exception 이 발생 했을때, catch 해서 원하는 로직대로 실행하도록 설정
     * 해당 경우에는, 원하는 ResponseEntity 를 반환하도록 개발
     */
    @ExceptionHandler(CalendarException.class)
    public ResponseEntity<ErrorResponse> handleCalendarException(CalendarException ex) {
        final ErrorCode errorCode = ex.getErrorCode();

        return new ResponseEntity<>(new ErrorResponse(errorCode, errorCode.getMessage()),
                ErrorCodeHttpStatusMapper.mapToStatus(errorCode));
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final ErrorCode errorCode;
        private final String errorMessage;
    }


}
