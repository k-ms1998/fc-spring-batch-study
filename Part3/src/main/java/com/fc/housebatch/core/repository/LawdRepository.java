package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface LawdRepository extends JpaRepository<Lawd, Long> {

    Optional<Lawd> findByLawdCd(String lawdCd);

    /**
     * select distinct substring(lawd_cd, 1, 5) from lawd where exists = 1 and lawd_cd not like '%0000000';
     * -> lawd ���� api ȣ�⿡ �ʿ��� ������ �ڵ�� ��������
     * -> �̶�, lawd_cd �� ó�� �ټ� ���ڸ� �������µ�, �ߺ����� �ʾƾ� �ϰ�, �������� ���� ���¿��ߵǰ�, ������ 8�ڸ��� 00000000�̸� ������ �ڵ尡 �ƴϱ� ������ ����
     * !! @Query �ȿ� ���� ���� �ۼ��ҋ�, DB�� ����� ���̺� �̸�(lawd)�� �ƴ� Entity �̸�(Lawd)���� �ۼ��ϰ�,
     *                                  ���� �� ���̺��� column ��(lawd_cd)�� �ƴ� Entity �� �Ķ���� ��(lawdCd)���� �ۼ� !!
     */
    @Query("select distinct substring(l.lawdCd, 1, 5) from Lawd l where l.exist = 1 and l.lawdCd not like '%0000000'")
    List<String> findAllDistinctGuLawdCd();
}
