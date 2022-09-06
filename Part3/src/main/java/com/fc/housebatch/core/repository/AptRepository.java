package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AptRepository extends JpaRepository<Apt, Long> {

    /**
     * JPA 에서 메서드 명으로 쿼리 작성하기:
     * aptName 이랑 jibun 값들이 일치하는 튜플들 가져오기
     */
    Optional<Apt> findByAptNameAndJibun(String aptName, String jibun);

}
