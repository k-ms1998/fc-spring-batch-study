package com.fc.project.core.domain.entity;

import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.Notification;
import com.fc.project.core.domain.Task;
import com.fc.project.core.domain.enums.ScheduleType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "schedules")
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PACKAGE)
public class Schedule extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;


    public static Schedule event(String title, String description, LocalDateTime startAt, LocalDateTime endAt, User user) {
        return Schedule.builder()
                .title(title)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .user(user)
                .scheduleType(ScheduleType.EVENT)
                .build();
    }

    public static Schedule notification(String title, LocalDateTime notifyAt, User user) {
        return Schedule.builder()
                .title(title)
                .startAt(notifyAt)
                .user(user)
                .scheduleType(ScheduleType.NOTIFICATION)
                .build();
    }

    public static Schedule task(String title, String description, LocalDateTime taskAt, User user) {
        return Schedule.builder()
                .title(title)
                .description(description)
                .startAt(taskAt)
                .user(user)
                .scheduleType(ScheduleType.TASK)
                .build();
    }

    public Event toEvent() {
        return new Event(this);
    }

    public Notification toNotification() {
        return new Notification(this);
    }

    public Task toTask() {
        return new Task(this);
    }

}
