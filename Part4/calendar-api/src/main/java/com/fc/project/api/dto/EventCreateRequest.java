package com.fc.project.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventCreateRequest {

    @NotBlank(message = "Title cannot be blank.")
    private final String title;
    private final String description;

    @NotNull
    private final LocalDateTime startAt;

    @NotNull
    private final LocalDateTime endAt;
    private final List<Long> attendeeIds;
}
