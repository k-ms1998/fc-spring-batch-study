package com.fc.housebatch.core.service;

import com.fc.housebatch.core.dto.AptDealDto;
import com.fc.housebatch.core.entity.Apt;
import com.fc.housebatch.core.entity.AptDeal;
import com.fc.housebatch.core.repository.AptDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AptDealService {

    private final AptService aptService;

    private final AptDealRepository aptDealRepository;

    @Transactional
    public void upsert(AptDealDto dto) {
        Apt apt = aptService.getAptOrNew(dto);
        saveAptDeal(dto, apt);

    }

    private AptDeal saveAptDeal(AptDealDto dto, Apt apt) {
        AptDeal aptDeal = findAptDeal(apt, dto);
        aptDeal.updateDealCanceledAndDate(dto.dealCanceledFromAptDealDto(), dto.dealCanceledDateFromAptDealDto());
        return aptDealRepository.save(aptDeal);
    }

    /**
     * 아파트 거래내역 찾고, 일치하는 거래내역이 없으면 새로 생성
     */
    public AptDeal findAptDeal(Apt apt, AptDealDto dto) {
        return aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apt, dto.getArea(), dto.dealDateFromAptDealDto(), dto.dealAmountFromAptDealDto(), dto.getFloor())
                .orElseGet(() -> AptDeal.of(apt, dto));
    }
}
