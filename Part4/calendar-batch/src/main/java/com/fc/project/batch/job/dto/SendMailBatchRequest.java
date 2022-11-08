package com.fc.project.batch.job.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"title", "email", "startAt"})
public class SendMailBatchRequest {

    private Long id;    // Schedule ID
    private LocalDateTime startAt;
    private String title;
    private String email;

}
