package com.points.PointsManagementProject.domain;

import com.points.PointsManagementProject.domain.BaseEntity.IdEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Table(name = "message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Message extends IdEntity {

    @Column(name = "user_id", length = 20)
    String userId;  // 유저 Id

    @Column(length = 200)
    String title;   // 제목

    String content; // 내용

    public Message(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    /**
     * 만료된 메시지 생성해주는 메서드
     * -> ex: title == '3000 포인트 만료', content == '2022-07-06 기준 3000 포인트가 만료되었습니다.'
     */
    public static Message expiredPointMessageInstance(String userId, Long amount, LocalDate expiredDate) {
        return new Message(userId,
                amount + " 포인트 만료",
                String.format("%s 기준 %s 포인트가 만료되었습니다.", expiredDate.format(DateTimeFormatter.ISO_DATE), amount));
    }

    /**
     * 만료 예정인 포인트에 대해서 메시지 생성해주는 메서드
     * -> ex: title == '3000 포인트 만료 예정', content == '2022-07-06 까지 3000 포인트가 만료 예정입니다.'
     */
    public static Message expiringSoonPointMessageInstance(String userId, Long amount, LocalDate expiredDate) {
        return new Message(userId,
                amount + " 포인트 만료 예정",
                String.format("%s 까지 %s 포인트가 만료 예정입니다.", expiredDate.format(DateTimeFormatter.ISO_DATE), amount));
    }
}
