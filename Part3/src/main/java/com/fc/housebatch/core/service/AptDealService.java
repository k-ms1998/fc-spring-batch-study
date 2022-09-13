package com.fc.housebatch.core.service;

import com.fc.housebatch.core.dto.AptDealDto;
import com.fc.housebatch.core.dto.AptDto;
import com.fc.housebatch.core.entity.Apt;
import com.fc.housebatch.core.entity.AptDeal;
import com.fc.housebatch.core.repository.AptDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * guLawdCd 랑 dealDate 파라미터 값들이 일치하는 AptDeal 데이터들 가져오기
     */
    public List<AptDeal> findGuLawdCdAndDealDate(String guLawdCd, LocalDate dealDate) {
        return aptDealRepository.findByDealCanceledIsFalseAndDealDateEquals(dealDate)
                .stream()
                .filter(aptDeal -> aptDeal.getApt().getGuLawdCd().equals(guLawdCd))
                .collect(Collectors.toList());
    }

    /**
     * AtpDeal -> AptDto 로 변환해서 반환하기
     */
    public List<AptDto> guLawdCdAndDealDateToAtpDto(String guLawdCd, LocalDate dealDate){
        return findGuLawdCdAndDealDate(guLawdCd, dealDate)
                .stream()
                .map(aptDeal -> new AptDto(aptDeal.getApt().getAptName(), aptDeal.getDealAmount()))
                .collect(Collectors.toList());
    }
}
