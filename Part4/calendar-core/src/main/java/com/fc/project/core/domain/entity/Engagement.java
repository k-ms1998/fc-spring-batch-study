package com.fc.project.core.domain.entity;

import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.enums.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Engagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private User attendee;

    @CreatedDate
    private LocalDateTime createdAt;
    private RequestStatus requestStatus;

    public Engagement(Schedule schedule, User attendee, LocalDateTime createdAt, RequestStatus requestStatus) {
        this.schedule = schedule;
        this.attendee = attendee;
        this.createdAt = createdAt;
        this.requestStatus = requestStatus;
    }
}
