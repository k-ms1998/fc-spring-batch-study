package com.points.PointsManagementProject.domain;

import com.points.PointsManagementProject.domain.BaseEntity.IdEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "point_wallet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointWallet extends IdEntity {

    Long amount;    // 보유 금액

    @Column(name = "user_id")
    Long userId; // 유저 Id

    public PointWallet(Long amount, Long userId) {
        this.amount = amount;
        this.userId = userId;
    }
}
