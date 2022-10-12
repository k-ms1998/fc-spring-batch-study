package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.EventCreateRequest;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.repository.EngagementRepository;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new RuntimeException("Overlapping Engagements");
        }

        final Schedule eventSchedule = Schedule.event(
                eventCreateRequest.getTitle(), eventCreateRequest.getDescription(),
                eventCreateRequest.getStartAt(), eventCreateRequest.getEndAt(), userService.findByUserIdOrThrow(authUser.getId()));

        scheduleRepository.save(eventSchedule);

        eventCreateRequest.getAttendeeIds().forEach(id -> {
            System.out.println("id = " + id);
            final User attendee = userService.findByUserIdOrThrow(id);
            final Engagement engagement = new Engagement(eventSchedule, attendee, RequestStatus.ACCEPTED);

            engagementRepository.save(engagement);
            emailService.sendEngagement(engagement);
        });
    }

}
