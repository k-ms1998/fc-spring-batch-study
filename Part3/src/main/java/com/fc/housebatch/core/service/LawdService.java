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
         * ���� �����ϸ� ������Ʈ:
         * �������ڵ�� unique �� ���̱� ������, �̹� ������ �������ڵ尡 DB�� �����ϸ� �̹� ���� �����ϴ� �� -> ������Ʈ
         *
         * ���� �������� ������ ���� �����ؼ� ����:
         * ������ �������ڵ带 ���� ���� DB�� �������� ������ ���� ���� �������� �ʱ� ������ ���� ����
         */
        Lawd saved = lawdRepository.findByLawdCd(lawd.getLawdCd())
                .orElseGet(() -> new Lawd()); // �����ϸ� ã���� Ʃ�� ��ȯ; �������� ������ ���ο� Lawd() �� �����ؼ� ��ȯ
        /**
         * ���� ������Ʈ
         */
        saved.updateLawdCd(lawd.getLawdCd());
        saved.updateLawdCDong(lawd.getLawdDong());
        saved.updateExist(lawd.getExist());

        /**
         * �� ����
         */
        lawdRepository.save(saved);
    }

}
