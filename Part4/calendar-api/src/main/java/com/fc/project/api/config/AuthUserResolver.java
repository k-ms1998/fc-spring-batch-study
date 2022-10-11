package com.fc.project.api.config;

import com.fc.project.api.dto.AuthUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.fc.project.api.service.LoginService.LOGIN_SESSION_KEY;

public class AuthUserResolver implements HandlerMethodArgumentResolver {

    /**
     * 파라미터가 원하는 타입인지(AuthUser) 확인
     * -> AuthUser 가 파라미터로 넘어오는지 확인
     * -> True 이면 resolveArgument 호출
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AuthUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        /**
         * Session 에서 LOGIN_SESSION_KEY 의 값을 가져오기
         * => 현재 로그인된 아이디의 userId 값 가져오기
         */
        final Long userId = (Long) webRequest.getAttribute(LOGIN_SESSION_KEY, WebRequest.SCOPE_SESSION);
        if (userId == null) {
            /**
             * 로그인이 되지 않은 상태
             */
            throw new RuntimeException("Bad request. No session");

        }

        return AuthUser.of(userId);
    }
}
