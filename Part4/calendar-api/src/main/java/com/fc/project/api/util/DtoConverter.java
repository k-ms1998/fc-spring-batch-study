package com.fc.project.api.util;

import com.fc.project.api.dto.scheduleDto.EventDto;
import com.fc.project.api.dto.scheduleDto.NotificationDto;
import com.fc.project.api.dto.scheduleDto.ScheduleDto;
import com.fc.project.api.dto.scheduleDto.TaskDto;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.enums.ScheduleType;

public abstract class DtoConverter {

    public static ScheduleDto fromSchedule(Schedule schedule){
        switch (schedule.getScheduleType()) {
            case TASK:
                return new TaskDto(schedule.getId(), schedule.getStartAt(),
                        schedule.getTitle(), schedule.getDescription(), schedule.getUser().getId());
            case EVENT:
                return new EventDto(schedule.getId(), schedule.getStartAt(),schedule.getEndAt(),
                        schedule.getTitle(), schedule.getDescription(), schedule.getUser().getId());
            case NOTIFICATION:
                return new NotificationDto(schedule.getId(), schedule.getStartAt(),
                        schedule.getTitle(), schedule.getUser().getId());
            default:
                throw new RuntimeException("Bad Request. Non-existent Schedule Type");
        }
    }
}
