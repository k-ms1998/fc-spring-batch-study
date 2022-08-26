package com.fc.housebatch.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * ����Ʈ �ǰŷ��� API�� ȣ���ϱ� ���� ����
 * 
 * ����Ʈ �ǰŷ��� API�� ȣ���ϱ� ���� �ʿ��� �Ķ����:
 * 1. serviceKey - API�� ȣ���ϱ� ���� ����Ű
 * 2. LAWD_CD - ������ �ڵ� 10�ڸ� �߾ּ� �� 5�ڸ� - guLawdCd; ex) 41135
 * 3. DEAL_YMD - �ŷ��� �߻��� ���; ex) 202107
 *
 */
@Slf4j
@Component
public class ApartmentApiResource {

    @Value("${external.apartment-api.path}") // application,yml ���� ������ �� ������
    private String path;

    @Value("${external.apartment-api.service-key}") // application,yml ���� ������ �� ������
    private String serviceKey;

    public Resource getResource(String lawdCd, YearMonth yearMonth) {
        String yearMonthStr = yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s", path, serviceKey, lawdCd, yearMonthStr);

        log.info("[ApartmentApiResource] URL: " + url);

        try {
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Failed to create UrlResource");
        }
    }
}
