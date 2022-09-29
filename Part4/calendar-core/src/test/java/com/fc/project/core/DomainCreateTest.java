package com.fc.project.core;

import com.fc.project.core.domain.Engagement;
import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.RequestStatus;
import com.fc.project.core.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DomainCreateTest {

    @Test
    void given_when_thenCreateEvent() throws Exception {
        // Given
        final User writer = new User("writer", "writer@email.com", "password_writer",
                LocalDate.of(1998, 1, 1), LocalDateTime.now());
        final User attendee = new User("attendee", "attendee@email.com", "password_attendee",
                LocalDate.of(1998, 1, 1), LocalDateTime.now());
        final Event event = new Event(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                "title", "description", writer, LocalDateTime.now());
        event.addEngagements(new Engagement(event, attendee, LocalDateTime.now(), RequestStatus.REQUESTED));

        // When

        // Then
        Assertions.assertEquals(event.getEngagements().get(0).getAttendee().getName(), "attendee");

    }
}
