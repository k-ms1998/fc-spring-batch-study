package com.fc.project.api.dto.scheduleDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class SharedScheduleDto {

    private final Long userId;
    private final String name;
    private final Boolean me;
    private final List<ScheduleDto> schedules;

    public SharedScheduleDto(Long userId, String name, Boolean me, List<ScheduleDto> schedules) {
        this.userId = userId;
        this.name = name;
        this.me = me;
        this.schedules = schedules;
    }
}
