package com.fc.project.api.controller;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.EventCreateRequest;
import com.fc.project.api.dto.NotificationCreateRequest;
import com.fc.project.api.dto.TaskCreateRequest;
import com.fc.project.api.dto.scheduleDto.ScheduleDto;
import com.fc.project.api.service.EventService;
import com.fc.project.api.service.NotificationService;
import com.fc.project.api.service.ScheduleQueryService;
import com.fc.project.api.service.TaskService;
import com.fc.project.core.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.fc.project.api.service.LoginService.LOGIN_SESSION_KEY;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final TaskService taskService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final ScheduleQueryService scheduleQueryService;

    @GetMapping("/day")
    public List<ScheduleDto> getSchedulesByDay(AuthUser authUser,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { // yyyy-MM-DD
        return scheduleQueryService.getScheduleByDay(authUser, date == null ? LocalDate.now() : date);
    }
    @GetMapping("/week")
    public List<ScheduleDto> getSchedulesByWeek(AuthUser authUser,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfWeek) { // yyyy-MM-DD
        return scheduleQueryService.getScheduleByWeek(authUser, startOfWeek == null ? LocalDate.now() : startOfWeek);
    }
    @GetMapping("/month")
    public List<ScheduleDto> getSchedulesByMonth(AuthUser authUser,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth) { // yyyy-MM
        return scheduleQueryService.getScheduleByMonth(authUser, yearMonth == null ? YearMonth.now() : YearMonth.parse(yearMonth));
    }

    @PostMapping("/tasks")
    public ResponseEntity<Void> createTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpSession httpSession,
                                           AuthUser authUser) {
        /**
         * AuthUserResolver 을 이용해서 AuthUser 를 파라미터로 넘겨줄때 로그인한 상태인지 아닌지 확인
         *
         */

        /**
         * userId 를 그대로 넘겨주기 않고, DTO 로 한번 변환해서 AuthUser 로 넘겨줌
         * => Because, Long 타입의 데이터를 많이 다루는데, userId 가 아닌 다른 Long 타입의 데이터를 실수로 넘겨주게 되면 오류를 확인하기 어려워짐
         *  => 그러므로, DTO 로 변환해서 넘겨줌
         * 모든 변수에 대해서 이럴 필요는 없고, TaskService 같은 어플리케이션 로직에서는 DTO 로 변환해주는걸 추천
         */
        taskService.create(taskCreateRequest, authUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/events")
    public ResponseEntity<Void> createEvent(@Valid @RequestBody EventCreateRequest eventCreateRequest, HttpSession httpSession,
                                            AuthUser authUser) {
        /**
         * AuthUserResolver 을 이용해서 AuthUser 를 파라미터로 넘겨줄때 로그인한 상태인지 아닌지 확인
         *
         */

        /**
         * userId 를 그대로 넘겨주기 않고, DTO 로 한번 변환해서 AuthUser 로 넘겨줌
         * => Because, Long 타입의 데이터를 많이 다루는데, userId 가 아닌 다른 Long 타입의 데이터를 실수로 넘겨주게 되면 오류를 확인하기 어려워짐
         *  => 그러므로, DTO 로 변환해서 넘겨줌
         * 모든 변수에 대해서 이럴 필요는 없고, TaskService 같은 어플리케이션 로직에서는 DTO 로 변환해주는걸 추천
         */
        eventService.create(eventCreateRequest, authUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/notifications")
    public ResponseEntity<Void> createNotification(@RequestBody NotificationCreateRequest notificationCreateRequest, HttpSession httpSession,
                                                   AuthUser authUser) {
        /**
         * AuthUserResolver 을 이용해서 AuthUser 를 파라미터로 넘겨줄때 로그인한 상태인지 아닌지 확인
         *
         */

        /**
         * userId 를 그대로 넘겨주기 않고, DTO 로 한번 변환해서 AuthUser 로 넘겨줌
         * => Because, Long 타입의 데이터를 많이 다루는데, userId 가 아닌 다른 Long 타입의 데이터를 실수로 넘겨주게 되면 오류를 확인하기 어려워짐
         *  => 그러므로, DTO 로 변환해서 넘겨줌
         * 모든 변수에 대해서 이럴 필요는 없고, TaskService 같은 어플리케이션 로직에서는 DTO 로 변환해주는걸 추천
         */
        notificationService.create(notificationCreateRequest, authUser);

        return ResponseEntity.ok().build();
    }
}
