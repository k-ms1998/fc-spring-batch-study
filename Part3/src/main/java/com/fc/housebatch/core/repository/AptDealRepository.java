package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Apt;
import com.fc.housebatch.core.entity.AptDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AptDealRepository extends JpaRepository<AptDeal, Long> {

    /**
     * JPA ���� �޼��� ������ ���� �ۼ��ϱ�:
     * apt, exclusiveArea, dealDate, dealAmount, floor �� ��ġ�ϴ� Ʃ�õ� ��������
     * (����Ʈ, �������, �ŷ� ��¥, �ŷ� �ݾ�, �ŷ� ������ ������ Unique �� �ŷ������̶�� �Ǵ�)
     */
    Optional<AptDeal> findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate dealDate, Long dealAmount, Integer floor);
}
