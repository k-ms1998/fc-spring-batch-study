package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface LawdRepository extends JpaRepository<Lawd, Long> {

    Optional<Lawd> findByLawdCd(String lawdCd);

    /**
     * select distinct substring(lawd_cd, 1, 5) from lawd where exists = 1 and lawd_cd not like '%0000000';
     * -> lawd 에서 api 호출에 필요한 법정동 코드들 가져오기
     * -> 이때, lawd_cd 의 처음 다섯 글자를 가져오는데, 중복되지 않아야 하고, 폐지되지 않은 상태여야되고, 마지막 8자리가 00000000이면 법정동 코드가 아니기 때문에 제외
     * !! @Query 안에 쿼리 문을 작성할떄, DB에 저장된 테이블 이름(lawd)이 아닌 Entity 이름(Lawd)으로 작성하고,
     *                                  변수 명도 테이블의 column 명(lawd_cd)이 아닌 Entity 의 파라미터 명(lawdCd)으로 작성 !!
     */
    @Query("select distinct substring(l.lawdCd, 1, 5) from Lawd l where l.exist = 1 and l.lawdCd not like '%0000000'")
    List<String> findAllDistinctGuLawdCd();
}
