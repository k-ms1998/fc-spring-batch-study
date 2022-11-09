package com.fc.project.api.controller;

import com.fc.project.api.service.EmailService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final EmailService emailService;

    @PostMapping("/mail")
    public ResponseEntity<Void> sendMail(@RequestBody List<SendMailBatchRequest> request) {
        request.forEach(s -> emailService.sendNotification(s));

        return ResponseEntity.ok().build();
    }

    /**
     * Batch.job.dto 에 있는 SendMailBatchRequest 와 동일한 dto
     * But, api 와 batch 모듈들은 core 모듈을 참조하고 있지만, api 와 core 끼리는 참조 X
     *  -> 그러므로, batch 에 있는 dto 를 참조 할 수 없는 문제 발생
     * 해결방안 1. 똑같은 dto 를 api 에도 만든다
     * 해결방안 2. 해당 dto 를 core 모듈로 옮긴다
     * 2번으로 했을때, 해당 dto 는 core 모듈에 맞지 않기 때문에 해결방안 1번으로 해결
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(of = {"title", "email", "startAt"})
    public static class SendMailBatchRequest {

        private Long id;    // Schedule ID
        private LocalDateTime startAt;
        private String title;
        private String email;

    }

}
