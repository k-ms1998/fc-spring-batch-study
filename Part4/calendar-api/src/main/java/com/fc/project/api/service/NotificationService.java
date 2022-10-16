package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.NotificationCreateRequest;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fc.project.api.dto.NotificationCreateRequest.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserService userService;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void create(NotificationCreateRequest notificationCreateRequest, AuthUser authUser) {
        final User user = userService.findByUserIdOrThrow(authUser.getId());
        final List<LocalDateTime> notifyAtList = notificationCreateRequest.createNotifyAtList();

        notifyAtList.forEach(notifyAt -> {
            final Schedule notificationSchedule = Schedule.notification( notificationCreateRequest.getTitle(), notifyAt, user);
            scheduleRepository.save(notificationSchedule);
        });
    }
}
