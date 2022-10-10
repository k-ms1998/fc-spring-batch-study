package com.fc.project.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TaskCreateRequest {

    private final String title;
    private final String description;
    private final LocalDateTime taskAt;

}
