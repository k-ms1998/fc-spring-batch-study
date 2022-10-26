package com.fc.project.api.exception;

import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

/**
 * Exception Handler:
 * Exception 이 발생했을때, exception 을 던지지 않고, Response 로  에러코드를 반환하는 방식으로 처리
 * Because, exception 을 던지는 방식은 overhead 가 많기 때문에
 *
 * @ControllerAdvice:
 * Specialization of @Component for classes that declare
 * @ExceptionHandler, @InitBinder, or @ModelAttribute methods to be shared across multiple @Controller classes.
 */
@ControllerAdvice
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

    /**
     * MethodArgumentNotValidException: Exception to be thrown when validation on an argument annotated with @Valid fails
     * 도메인에 대해서, @Valid 로 검증할 파라미터들 중에서 exception 이 발생했을때 catch 해서 오류 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationAnnotationException(MethodArgumentNotValidException ex) {
        final ErrorCode errorCode = ErrorCode.VALIDATION_FAIL;

        return new ResponseEntity<>(new ErrorResponse(
                        errorCode,
                        Optional.ofNullable(ex.getBindingResult().getFieldError())
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .orElse(errorCode.getMessage())),
                ErrorCodeHttpStatusMapper.mapToStatus(errorCode));
    }
    


    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final ErrorCode errorCode;
        private final String errorMessage;
    }


}
