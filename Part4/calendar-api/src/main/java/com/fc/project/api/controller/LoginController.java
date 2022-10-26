package com.fc.project.api.controller;

import com.fc.project.api.dto.LoginRequest;
import com.fc.project.api.dto.SignUpRequest;
import com.fc.project.api.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpRequest, HttpSession httpSession) {
        loginService.signUp(signUpRequest, httpSession);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpSession httpSession) {
        loginService.login(loginRequest, httpSession);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/logout")
    public ResponseEntity<Void> logout(HttpSession httpSession) {
        loginService.logout(httpSession);

        return ResponseEntity.ok().build();
    }
}
