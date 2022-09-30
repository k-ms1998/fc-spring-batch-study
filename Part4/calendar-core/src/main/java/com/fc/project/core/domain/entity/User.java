package com.fc.project.core.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthDay;

    @CreatedDate
    private LocalDateTime createdAt;

    public User(String name, String email, String password, LocalDate birthDay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
    }
}
