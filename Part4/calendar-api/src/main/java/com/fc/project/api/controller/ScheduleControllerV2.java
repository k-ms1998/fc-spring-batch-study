package com.fc.project.api.controller;

import com.fc.project.api.dto.*;
import com.fc.project.api.dto.scheduleDto.SharedScheduleDto;
import com.fc.project.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/schedules/v2")
@RequiredArgsConstructor
public class ScheduleControllerV2 {

    private final ScheduleQueryService scheduleQueryService;

    @GetMapping("/day")
    public List<SharedScheduleDto> getSchedulesByDay(AuthUser authUser,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { // yyyy-MM-DD
        return scheduleQueryService.getSharedScheduleByDay(authUser, date == null ? LocalDate.now() : date);
    }

    @GetMapping("/week")
    public List<SharedScheduleDto> getSchedulesByWeek(AuthUser authUser,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfWeek) { // yyyy-MM-DD
        return scheduleQueryService.getSharedScheduleByWeek(authUser, startOfWeek == null ? LocalDate.now() : startOfWeek);
    }

    @GetMapping("/month")
    public List<SharedScheduleDto> getSchedulesByMonth(AuthUser authUser,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth) { // yyyy-MM
        return scheduleQueryService.getSharedScheduleByMonth(authUser, yearMonth == null ? YearMonth.now() : YearMonth.parse(yearMonth));
    }

}
