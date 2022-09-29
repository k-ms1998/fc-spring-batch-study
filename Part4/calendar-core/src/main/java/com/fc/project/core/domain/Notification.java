package com.fc.project.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Notification {

    private Long id;
    private LocalDateTime notifyAt;
    private String title;
    private User user;
    private LocalDateTime createdAt;
}
