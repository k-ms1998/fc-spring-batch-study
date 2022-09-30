package com.fc.project.core.domain;

import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Event {

    private Schedule schedule;

    public Event(Schedule schedule) {
        this.schedule = schedule;
    }
}
