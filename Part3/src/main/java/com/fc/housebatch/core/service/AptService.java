package com.fc.housebatch.core.service;

import com.fc.housebatch.core.dto.AptDealDto;
import com.fc.housebatch.core.entity.Apt;
import com.fc.housebatch.core.entity.AptDeal;
import com.fc.housebatch.core.repository.AptDealRepository;
import com.fc.housebatch.core.repository.AptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AptDealDto -> Apt, AptDeal
 */
@Service
@RequiredArgsConstructor
public class AptService {

    private final AptRepository aptRepository;

    /**
     * 아파트 이름과 지번으로 찾고, 일치하는 값이 없으면 데이터 생성
     */
    public Apt getAptOrNew(AptDealDto dto) {
        Apt apt = aptRepository.findByAptNameAndJibun(dto.getAptName(),  dto.getJibun())
                .orElseGet(() -> Apt.of(dto));
        return aptRepository.save(apt);
    }


}
