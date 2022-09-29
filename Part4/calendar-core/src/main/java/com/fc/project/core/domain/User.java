package com.fc.project.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthDay;
    private LocalDateTime createdAt;

    public User(String name, String email, String password, LocalDate birthDay, LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
        this.createdAt = createdAt;
    }
}
