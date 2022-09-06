package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Apt;
import com.fc.housebatch.core.entity.AptDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AptDealRepository extends JpaRepository<AptDeal, Long> {

    /**
     * JPA 에서 메서드 명으로 쿼리 작성하기:
     * apt, exclusiveArea, dealDate, dealAmount, floor 이 일치하는 튜플들 가져오기
     * (아파트, 전용면적, 거래 날짜, 거래 금액, 거래 층수가 같으면 Unique 한 거래내역이라고 판단)
     */
    Optional<AptDeal> findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate dealDate, Long dealAmount, Integer floor);
}
