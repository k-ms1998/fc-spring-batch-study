package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.scheduleDto.ScheduleDto;
import com.fc.project.api.dto.scheduleDto.SharedScheduleDto;
import com.fc.project.api.util.DtoConverter;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.repository.EngagementRepository;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.service.UserService;
import com.fc.project.core.util.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService {

    private final ShareService shareService;
    private final UserService userService;

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

    public List<SharedScheduleDto> getSharedScheduleByDay(AuthUser authUser, LocalDate date) {
        return getSharedScheduleByFunction(authUser,
                (Long userId) -> getScheduleByDay(AuthUser.of(userId), date));
    }

    public List<SharedScheduleDto> getSharedScheduleByWeek(AuthUser authUser, LocalDate startOfWeek) {
        return getSharedScheduleByFunction(authUser,
                (Long userId) -> getScheduleByWeek(AuthUser.of(userId), startOfWeek));
    }

    public List<SharedScheduleDto> getSharedScheduleByMonth(AuthUser authUser, YearMonth yearMonth) {
        return getSharedScheduleByFunction(authUser,
                (Long userId) -> getScheduleByMonth(AuthUser.of(userId), yearMonth));
    }

    /**
     * getSharedScheduleByDay, getSharedScheduleByWeek, getSharedScheduleByMonth
     * 모두 userId, name, me 를 설정하는 부분은 똑같이 중복되며, schedules 를 설정하는 부분만 다릅니다
     * 세 개가 schedules 를 설정하는 방법은 각각 getScheduleByDay, getScheduleByWeek, getScheduleByMonth 메서드를 호출하는 거였으므로,
     * 각각 다른 메서드들을 인자로 넘겨주는 새로운 메서드(getSharedScheduleByFunction)를 생성합니다
     *
     */
    private List<SharedScheduleDto> getSharedScheduleByFunction(AuthUser authUser,
                                                               Function<Long, List<ScheduleDto>> function) {
        return shareService.findSharedUserIdsByUser(authUser).stream()
                .map(userId -> SharedScheduleDto.builder()
                        .userId(userId)
                        .name(userService.findByUserIdOrThrow(userId).getName())
                        .me(userId.equals(authUser.getId()))
                        .schedules(function.apply(userId)) // 인자로 받은 메서드를 호출했을때 반환되는 값을 schedules 로 설정
                        .build())
                .collect(Collectors.toList());
    }
}
