package com.fc.project.api.util;

import com.fc.project.api.dto.scheduleDto.EventDto;
import com.fc.project.api.dto.scheduleDto.NotificationDto;
import com.fc.project.api.dto.scheduleDto.ScheduleDto;
import com.fc.project.api.dto.scheduleDto.TaskDto;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.enums.ScheduleType;
import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;

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
                throw new CalendarException(ErrorCode.BAD_REQUEST);
        }
    }
}
