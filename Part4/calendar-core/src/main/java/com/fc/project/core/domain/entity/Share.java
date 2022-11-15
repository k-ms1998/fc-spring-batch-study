package com.fc.project.core.domain.entity;

import com.fc.project.core.domain.Event;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.util.Period;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shares")
public class Share extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;
    private Long toUserId;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    public Share(Long fromUserId, Long toUserId, RequestStatus requestStatus, Direction direction) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.requestStatus = requestStatus;
        this.direction = direction;
    }

    public Share reply(RequestStatus type) {
        switch (type){
            case ACCEPTED:
                this.requestStatus = RequestStatus.ACCEPTED;
                break;
            case REJECTED:
                this.requestStatus = RequestStatus.REJECTED;
                break;
            default:
                break;
        }

        return this;
    }

    public enum Direction {
        BI_DIRECTIONAL, UNI_DIRECTIONAL
    }

}

