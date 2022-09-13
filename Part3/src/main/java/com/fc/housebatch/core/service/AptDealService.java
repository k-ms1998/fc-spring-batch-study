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
     * ����Ʈ �ŷ����� ã��, ��ġ�ϴ� �ŷ������� ������ ���� ����
     */
    public AptDeal findAptDeal(Apt apt, AptDealDto dto) {
        return aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                apt, dto.getArea(), dto.dealDateFromAptDealDto(), dto.dealAmountFromAptDealDto(), dto.getFloor())
                .orElseGet(() -> AptDeal.of(apt, dto));
    }

    /**
     * guLawdCd �� dealDate �Ķ���� ������ ��ġ�ϴ� AptDeal �����͵� ��������
     */
    public List<AptDeal> findGuLawdCdAndDealDate(String guLawdCd, LocalDate dealDate) {
        return aptDealRepository.findByDealCanceledIsFalseAndDealDateEquals(dealDate)
                .stream()
                .filter(aptDeal -> aptDeal.getApt().getGuLawdCd().equals(guLawdCd))
                .collect(Collectors.toList());
    }

    /**
     * AtpDeal -> AptDto �� ��ȯ�ؼ� ��ȯ�ϱ�
     */
    public List<AptDto> guLawdCdAndDealDateToAtpDto(String guLawdCd, LocalDate dealDate){
        return findGuLawdCdAndDealDate(guLawdCd, dealDate)
                .stream()
                .map(aptDeal -> new AptDto(aptDeal.getApt().getAptName(), aptDeal.getDealAmount()))
                .collect(Collectors.toList());
    }
}
