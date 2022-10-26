package com.fc.project.api.exception;

import com.fc.project.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * ErrorCode -> HttpStatus 로 변환하는 helper class
 * 해당 클래스의 모든 변수는 static 이므로, 추상 클래스로 선언
 *  -> 생성자 선언 불가능
 */
public abstract class ErrorCodeHttpStatusMapper {

    public static HttpStatus mapToStatus(ErrorCode errorCode) {
        switch (errorCode) {
            case USER_ALREADY_EXISTS:
            case VALIDATION_FAIL:
            case BAD_REQUEST:
            case EVENT_CREATE_OVERLAPPED:
                return HttpStatus.BAD_REQUEST;

            case USER_NOT_FOUND:
            case PASSWORD_NOT_MATCH:
                return HttpStatus.UNAUTHORIZED;

            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }
    
}
