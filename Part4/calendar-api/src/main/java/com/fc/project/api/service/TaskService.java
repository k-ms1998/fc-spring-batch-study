package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.TaskCreateRequest;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserService userService;
    private final ScheduleRepository scheduleRepository;

    public void create(TaskCreateRequest taskCreateRequest, AuthUser authUser) {
        final Schedule taskSchedule = Schedule.task(taskCreateRequest.getTitle(), taskCreateRequest.getDescription(),
                                                        taskCreateRequest.getTaskAt(),userService.findByUserIdOrThrow(authUser.getId()));
        scheduleRepository.save(taskSchedule);
    }
}
