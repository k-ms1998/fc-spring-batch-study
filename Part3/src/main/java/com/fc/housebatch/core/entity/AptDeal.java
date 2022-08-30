package com.fc.housebatch.core.entity;

import com.fc.housebatch.core.dto.AptDealDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "apt_deal")
public class AptDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_deal_id")
    private Long aptDealId;

    @ManyToOne
    @JoinColumn(name = "apt_id")
    private Apt apt;

    @Column(name = "exclusive_area", nullable = false)
    private Double exclusiveArea;

    @Column(name = "deal_date")
    private LocalDate dealDate;

    @Column(name = "deal_amount", nullable = false)
    private Long dealAmount;

    @Column(nullable = false)
    private Integer floor;

    @Column(name = "deal_canceled")
    private Boolean dealCanceled;

    @Column(name = "deal_canceled_date")
    private LocalDate dealCanceledDate;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public AptDeal(Double exclusiveArea, LocalDate dealDate, Long dealAmount, Integer floor, Boolean dealCanceled, LocalDate dealCanceledDate) {
        this.exclusiveArea = exclusiveArea;
        this.dealDate = dealDate;
        this.dealAmount = dealAmount;
        this.floor = floor;
        this.dealCanceled = dealCanceled;
        this.dealCanceledDate = dealCanceledDate;
    }

    public AptDeal from(AptDealDto aptDealDto) {
        Double exclusiveArea = aptDealDto.getArea();
        LocalDate dealDate = aptDealDto.dealDateFromAptDealDto();
        Long dealAmount = aptDealDto.dealAmountFromAptDealDto();
        Integer floor = aptDealDto.getFloor();
        Boolean dealCanceled = aptDealDto.dealCanceledFromAptDealDto();
        LocalDate dealCanceledDate = aptDealDto.dealCanceledDateFromAptDealDto();

        return new AptDeal(exclusiveArea, dealDate, dealAmount, floor, dealCanceled, dealCanceledDate);
    }


}
