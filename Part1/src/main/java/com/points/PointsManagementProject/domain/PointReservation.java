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
@Table(name = "point_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointReservation extends IdEntity {

    Long amount;    // 적립 금액

    @Column(name = "earned_date")
    LocalDate earnedDate;   // 적립 일자

    @Column(name = "available_days")
    int availableDays; // 유효기간

    @Column(name = "is_executed")
    boolean isExecuted; // 적용여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_wallet_id")
    PointWallet pointWallet;

    public PointReservation(Long amount, LocalDate earnedDate, int availableDays, PointWallet pointWallet) {
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.availableDays = availableDays;
        this.pointWallet = pointWallet;
        this.isExecuted = false;
    }

    public void executed() {
        this.isExecuted = true;
    }

    public LocalDate getExpiryDate() {
        return this.earnedDate.plusDays(this.availableDays);
    }
}
