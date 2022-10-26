package com.fc.project.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    private final String name;

    @Email
    @NotBlank
    private final String email;

    @Size(min = 6, message = "4자리 이상 입력해주세요.")
    @NotBlank
    private final String password;

    @NotBlank
    private final LocalDate birthDay;

    public SignUpRequest(String name, String email, String password, LocalDate birthDay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
    }
}
