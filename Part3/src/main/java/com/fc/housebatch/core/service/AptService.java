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
     * ����Ʈ �̸��� �������� ã��, ��ġ�ϴ� ���� ������ ������ ����
     */
    public Apt getAptOrNew(AptDealDto dto) {
        Apt apt = aptRepository.findByAptNameAndJibun(dto.getAptName(),  dto.getJibun())
                .orElseGet(() -> Apt.of(dto));
        return aptRepository.save(apt);
    }


}
