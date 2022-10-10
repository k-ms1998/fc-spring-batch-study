package com.fc.project.core;

import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.domain.enums.ScheduleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EnableJpaAuditing
public class DomainCreateTest {

    @Test
    void given_when_thenCreateEvent() throws Exception {
        // Given
        final User user = new User("me", "me@email.com", "myPassword", LocalDate.of(1998, 1, 1));
        final Schedule taskSchedule = Schedule.task("Task", "Clean Up", LocalDateTime.now(), user);

        // When

        // Then
        Assertions.assertEquals(taskSchedule.getScheduleType(), ScheduleType.TASK);
        Assertions.assertEquals(taskSchedule.getTitle().equals("Task"), true);
        Assertions.assertEquals(taskSchedule.getDescription().equals("Clean Up"), true);
        System.out.println("taskSchedule.getCreatedAt() = " + taskSchedule.getCreatedAt());

    }
}
