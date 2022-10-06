package com.fc.project.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignUpRequest {

    private final String name;
    private final String email;
    private final String password;
    private final LocalDate birthDay;

    public SignUpRequest(String name, String email, String password, LocalDate birthDay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
    }
}
