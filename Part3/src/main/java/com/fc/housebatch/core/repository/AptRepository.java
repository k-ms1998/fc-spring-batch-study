package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AptRepository extends JpaRepository<Apt, Long> {

    /**
     * JPA ���� �޼��� ������ ���� �ۼ��ϱ�:
     * aptName �̶� jibun ������ ��ġ�ϴ� Ʃ�õ� ��������
     */
    Optional<Apt> findByAptNameAndJibun(String aptName, String jibun);

}
