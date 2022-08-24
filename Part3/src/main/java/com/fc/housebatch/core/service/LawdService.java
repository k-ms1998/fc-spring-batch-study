package com.fc.housebatch.core.service;

import com.fc.housebatch.core.entity.Lawd;
import com.fc.housebatch.core.repository.LawdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LawdService {

    private final LawdRepository lawdRepository;

    @Transactional
    public void upsertLawd(Lawd lawd) {
        /**
         * 값이 존재하면 업데이트:
         * 법정동코드는 unique 한 값이기 때문에, 이미 동일한 법정동코드가 DB에 존재하면 이미 값이 존재하는 것 -> 업데이트
         *
         * 값이 존재하지 않으면 새로 생성해서 저장:
         * 동일한 법정동코드를 가진 값이 DB에 존재하지 않으면 값이 아직 존재하지 않기 때문에 새로 생성
         */
        Lawd saved = lawdRepository.findByLawdCd(lawd.getLawdCd())
                .orElseGet(() -> new Lawd()); // 존재하면 찾으면 튜플 반환; 존재하지 않으면 새로운 Lawd() 를 생성해서 반환
        /**
         * 값들 업데이트
         */
        saved.updateLawdCd(lawd.getLawdCd());
        saved.updateLawdCDong(lawd.getLawdDong());
        saved.updateExist(lawd.getExist());

        /**
         * 값 저장
         */
        lawdRepository.save(saved);
    }

}
