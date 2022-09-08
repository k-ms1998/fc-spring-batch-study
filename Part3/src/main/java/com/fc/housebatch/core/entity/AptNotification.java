package com.fc.housebatch.core.entity;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "apt_notification")
@EntityListeners(AuditingEntityListener.class)
public class AptNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_notification_id")
    private Long aptNotificationId;

    @Column(nullable = false)
    private String email;

    @Column(name = "gu_lawd_cd", nullable = false)
    private String guLawdCd;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
