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
        return getSchedulesAndEngagementsByPeriod(authUser.getId(),
                Period.of(date));
    }

    public List<ScheduleDto> getScheduleByWeek(AuthUser authUser, LocalDate startOfWeek) {
        return getSchedulesAndEngagementsByPeriod(authUser.getId(),
                Period.of(startOfWeek, startOfWeek.plusDays(6)));
    }

    public List<ScheduleDto> getScheduleByMonth(AuthUser authUser, YearMonth yearMonth) {
        return getSchedulesAndEngagementsByPeriod(authUser.getId(),
                Period.of(yearMonth.atDay(1), yearMonth.atEndOfMonth()));
    }

    private List<ScheduleDto> getSchedulesAndEngagementsByPeriod(Long userId, Period period) {
        final Stream<ScheduleDto> schedules =
                scheduleRepository.findAllByUserId(userId).stream()
                        .filter(s -> s.isOverlapped(period))
                        .map(s -> DtoConverter.fromSchedule(s));

        final Stream<ScheduleDto> engagements =
                engagementRepository.findByAttendeeId(userId).stream()
                        .filter(e -> e.isOverlapped(period))
                        .map(e -> DtoConverter.fromSchedule(e.getSchedule()));

        return Stream.concat(schedules, engagements)
                .collect(Collectors.toList());
    }
}
