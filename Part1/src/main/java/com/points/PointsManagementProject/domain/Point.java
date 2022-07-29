package com.points.PointsManagementProject.domain;

import com.points.PointsManagementProject.domain.BaseEntity.IdEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "point")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends IdEntity {

    Long amount;    // 적립 금액

    @Column(name = "earned_date")
    LocalDate earnedDate;   // 적립 일자

    @Column(name = "expire_date")
    LocalDate expiredDate;  // 만료 일자

    @Column(name = "is_used")
    boolean isUsed; // 사용 유뮤

    @Column(name = "is_expired")
    boolean isExpired;  // 만료여부

    /**
     * N(point) : 1(wallet) 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_wallet_id") // point_wallet_id 컬럼으로 조인
    PointWallet pointWallet;


    public Point(Long amount, LocalDate earnedDate, LocalDate expiredDate, PointWallet pointWallet) {
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.expiredDate = expiredDate;
        this.pointWallet = pointWallet;
        this.isUsed = false;
        this.isExpired = false;
    }

    public Point(Long amount, LocalDate earnedDate, LocalDate expiredDate, PointWallet pointWallet, boolean isExpired) {
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.expiredDate = expiredDate;
        this.pointWallet = pointWallet;
        this.isUsed = false;
        this.isExpired = isExpired;
    }

    public void expire() {
        this.isExpired = true;
    }
}
