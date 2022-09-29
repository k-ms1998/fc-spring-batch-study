package com.fc.project.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Event {

    private Long id;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String title;
    private String description;
    private User user;
    private List<Engagement> engagements;
    private LocalDateTime createdAt;

    public Event(LocalDateTime startAt, LocalDateTime endAt, String title, String description, User user, LocalDateTime createdAt) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;

        this.engagements = new ArrayList<>();
    }

    public void addEngagements(Engagement engagement) {
        if (engagements == null) {
            engagements = new ArrayList<>();
        }
        engagements.add(engagement);
    }
}
