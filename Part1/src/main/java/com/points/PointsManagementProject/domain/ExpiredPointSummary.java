package com.points.PointsManagementProject.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import javax.persistence.Entity;

@Getter
public class ExpiredPointSummary{
    String userId;
    Long amount;    // 만료 금액

    /**
     * @QueryProjection: 생성자를 QueryDsl 에서 사용할 수 있도록 해주는 애노테이션
     */
    @QueryProjection
    public ExpiredPointSummary(String userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }
}