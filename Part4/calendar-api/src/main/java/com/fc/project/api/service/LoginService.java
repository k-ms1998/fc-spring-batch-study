package com.fc.project.api.service;

import com.fc.project.api.dto.LoginRequest;
import com.fc.project.api.dto.SignUpRequest;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.dto.UserCreateRequest;
import com.fc.project.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * LoginService
 *  -> Application Service Layer
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService {

    public final static String LOGIN_SESSION_KEY = "USER_ID";
    private final UserService userService;

    /**
     * 회원가입 하기
     * 회원 가입 != User 생성하기
     *  -> 그로므로, UserService 랑은 기능이 분리됨
     */
    @Transactional
    public void signUp(SignUpRequest signUpRequest, HttpSession session) {
        /**
         * UserService 에서 Create 를 담당 & 이미 존재하는 회원 검증도 UserService 담당
         * 생성이되면 Session 에 담고 Return
         */
        final User user = userService.create(new UserCreateRequest(
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getBirthDay()
        ));
        session.setAttribute(LOGIN_SESSION_KEY, user.getId());
    }

    @Transactional
    public void login(LoginRequest loginRequest, HttpSession session) {
        /**
         * Session 값이 있으면 Return
         * Session 값이 없으면 비밀번호 검증 후 로그인 & Session 에 담아서 Return
         */
        final Long userId = (Long) session.getAttribute(LOGIN_SESSION_KEY);
        if (userId != null) {
            return;
        }

        final Optional<User> user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (user.isPresent()) {
            session.setAttribute(LOGIN_SESSION_KEY, user.get().getId());
        }else{
            throw new RuntimeException("Password or Email Doesn't Match");
        }
    }

    public void logout(HttpSession session) {
        /**
         * Session 제거
         */
        session.removeAttribute(LOGIN_SESSION_KEY);
    }
}
