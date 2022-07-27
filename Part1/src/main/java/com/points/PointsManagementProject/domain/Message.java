package com.points.PointsManagementProject.domain;

import com.points.PointsManagementProject.domain.BaseEntity.IdEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends IdEntity {

    @Column(name = "user_id", length = 20)
    String userId;  // 유저 Id

    @Column(length = 200)
    String title;   // 제목

    String content; // 내용


}
