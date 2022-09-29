package com.fc.project.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Engagement {

    private Long id;
    private Event event;
    private User attendee;
    private LocalDateTime createdAt;
    private RequestStatus requestStatus;

    public Engagement(Event event, User attendee, LocalDateTime createdAt, RequestStatus requestStatus) {
        this.event = event;
        this.attendee = attendee;
        this.createdAt = createdAt;
        this.requestStatus = requestStatus;
    }
}
