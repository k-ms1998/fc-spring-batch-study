package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.scheduleDto.ScheduleDto;
import com.fc.project.api.util.DtoConverter;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.repository.EngagementRepository;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.util.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;
    private final EngagementRepository engagementRepository;

    public List<ScheduleDto> getScheduleByDay(AuthUser authUser, LocalDate date) {
        final Stream<ScheduleDto> schedules =
                scheduleRepository.findAllByUserId(authUser.getId()).stream()
                .filter(s -> s.isOverlapped(date))
                .map(s -> DtoConverter.fromSchedule(s));

        final Stream<ScheduleDto> engagements =
                engagementRepository.findByAttendeeId(authUser.getId()).stream()
                .filter(e -> e.isOverlapped(date))
                .map(e -> DtoConverter.fromSchedule(e.getSchedule()));


        return Stream.concat(schedules, engagements)
                .collect(Collectors.toList());
    }

    public List<ScheduleDto> getScheduleByWeek(AuthUser authUser, LocalDate startOfWeek) {
        final Period period = Period.of(startOfWeek, startOfWeek.plusDays(6));

        final Stream<ScheduleDto> schedules =
                scheduleRepository.findAllByUserId(authUser.getId()).stream()
                        .filter(s -> s.isOverlapped(period))
                        .map(s -> DtoConverter.fromSchedule(s));

        final Stream<ScheduleDto> engagements =
                engagementRepository.findByAttendeeId(authUser.getId()).stream()
                        .filter(e -> e.isOverlapped(period))
                        .map(e -> DtoConverter.fromSchedule(e.getSchedule()));

        return Stream.concat(schedules, engagements)
                .collect(Collectors.toList());
    }

    public List<ScheduleDto> getScheduleByMonth(AuthUser authUser, YearMonth yearMonth) {
        final Period period = Period.of(yearMonth.atDay(1), yearMonth.atEndOfMonth());

        final Stream<ScheduleDto> schedules =
                scheduleRepository.findAllByUserId(authUser.getId()).stream()
                        .filter(s -> s.isOverlapped(period))
                        .map(s -> DtoConverter.fromSchedule(s));

        final Stream<ScheduleDto> engagements =
                engagementRepository.findByAttendeeId(authUser.getId()).stream()
                        .filter(e -> e.isOverlapped(period))
                        .map(e -> DtoConverter.fromSchedule(e.getSchedule()));

        return Stream.concat(schedules, engagements)
                .collect(Collectors.toList());
    }
}
