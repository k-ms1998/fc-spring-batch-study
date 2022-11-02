package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.EngagementEmail;
import com.fc.project.api.dto.EventCreateRequest;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import com.fc.project.core.repository.EngagementRepository;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.service.UserService;
import com.fc.project.core.util.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EmailService emailService;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;
    private final EngagementRepository engagementRepository;

    @Transactional
    public void create(EventCreateRequest eventCreateRequest, AuthUser authUser) {
        //이벤트 참여자가 다른 이벤트와 중복되면 안됨
        List<Engagement> overlappingEngagements = eventCreateRequest.getAttendeeIds().stream()
                .map(e -> engagementRepository.findByAttendeeId(e))
                .flatMap(List::stream)// List<List> -> List 로 변환
                .collect(Collectors.toList());
        System.out.println("overlappingEngagements = " + overlappingEngagements);

        final Long overlappingEngagementsCount = overlappingEngagements.stream()
                .filter(e -> e != null)
                .filter(e -> e.getRequestStatus() == RequestStatus.ACCEPTED)
                .map(e -> e.getSchedule().toEvent())
                .filter(e -> e.isOverlapped(eventCreateRequest.getStartAt(), eventCreateRequest.getEndAt()))
                .count();
        if (overlappingEngagementsCount > 0L) {
            throw new CalendarException(ErrorCode.EVENT_CREATE_OVERLAPPED);
        }

        final Schedule eventSchedule = Schedule.event(
                eventCreateRequest.getTitle(), eventCreateRequest.getDescription(),
                eventCreateRequest.getStartAt(), eventCreateRequest.getEndAt(), userService.findByUserIdOrThrow(authUser.getId()));

        scheduleRepository.save(eventSchedule);

        final List<User> attendees = eventCreateRequest.getAttendeeIds().stream()
                .map(id -> userService.findByUserIdOrThrow(id))
                .collect(Collectors.toList());
        final List<String> attendeeEmails = attendees.stream().map(u -> u.getEmail())
                .collect(Collectors.toList());

        attendees.forEach(u -> {
            final Engagement engagement = new Engagement(eventSchedule, u, RequestStatus.REQUESTED);

            engagementRepository.save(engagement);
            emailService.sendEngagement(EngagementEmail.builder()
                    .engagementId(engagement.getId())
                    .title(engagement.getSchedule().getTitle())
                    .recipient(engagement.getAttendee().getEmail())
                    .attendeeEmails(attendeeEmails)
                    .period(engagement.getPeriod())
                    .build());
        });
    }

}
