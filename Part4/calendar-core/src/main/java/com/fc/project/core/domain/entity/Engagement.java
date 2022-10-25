package com.fc.project.core.domain.entity;

import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.util.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@ToString(of = {"id", "requestStatus"})
public class Engagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "attendee_id")
    private User attendee;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    public Engagement(Schedule schedule, User attendee, RequestStatus requestStatus) {
        this.schedule = schedule;
        this.attendee = attendee;
        this.requestStatus = requestStatus;
    }

    public boolean isOverlapped(LocalDate date){
        return this.getSchedule().isOverlapped(date);
    }

    public boolean isOverlapped(Period period) {
        return this.getSchedule().isOverlapped(period);
    }
}
