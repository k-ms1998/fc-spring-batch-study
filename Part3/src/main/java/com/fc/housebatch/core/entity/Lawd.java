package com.fc.housebatch.core.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "lawd")
public class Lawd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lawd_cd", nullable = false)
    private String lawdCd;

    @Column(name = "lawd_dong", nullable = false)
    private String lawdDong;

    @Column(nullable = false)
    private Boolean exist;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Lawd(String lawdCd, String lawdDong, Boolean exist) {
        this.lawdCd = lawdCd;
        this.lawdDong = lawdDong;
        this.exist = exist;
    }
}
