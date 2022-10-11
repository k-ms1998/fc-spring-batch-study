package com.fc.project.api.config.dev;

import com.fc.project.api.config.AuthUserResolver;
import com.fc.project.api.dto.AuthUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 테스트할때마다 로그인을 하고, 생성된 Session Id 값을 가져오는 번거로운을 없애기 위함
 * 테스트할때 로그인 작업을 거치지 않고 AuthUser 생성
 * -> 실제로  로그인 후, Session 을 통해서 userId 값을 가져오는 것이 아니라, url 파라미터로 userId 값 받기
 */
public class FakeAuthUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AuthUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final String userIdStr = webRequest.getParameter("userId"); // ex: /schedules/tasks?userId=1 에서 userId=1 가져옴
        if (userIdStr == null) {
            return new AuthUserResolver().resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }

        return AuthUser.of(Long.parseLong(userIdStr));
    }
}
