package com.fc.project.api.controller;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.TaskCreateRequest;
import com.fc.project.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.fc.project.api.service.LoginService.LOGIN_SESSION_KEY;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<Void> createTask(@RequestBody TaskCreateRequest taskCreateRequest, HttpSession httpSession) {
        /*
        로그인 했는지 확인
         */
        final Long userId = (Long) httpSession.getAttribute(LOGIN_SESSION_KEY);
        if (userId == null) {
            throw new RuntimeException("Bad request. No session");
        }

        /**
         * userId 를 그대로 넘겨주기 않고, DTO 로 한번 변환해서 AuthUser 로 넘겨줌
         * => Because, Long 타입의 데이터를 많이 다루는데, userId 가 아닌 다른 Long 타입의 데이터를 실수로 넘겨주게 되면 오류를 확인하기 어려워짐
         *  => 그러므로, DTO 로 변환해서 넘겨줌
         * 모든 변수에 대해서 이럴 필요는 없고, TaskService 같은 어플리케이션 로직에서는 DTO 로 변환해주는걸 추천
         */
        taskService.create(taskCreateRequest, AuthUser.of(userId));

        return ResponseEntity.ok().build();
    }
}
