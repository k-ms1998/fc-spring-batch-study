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
 * 아파트 실거래가 API를 호출하기 위한 설정
 * 
 * 아파트 실거래가 API를 호출하기 위해 필요한 파라미터:
 * 1. serviceKey - API를 호출하기 위한 인증키
 * 2. LAWD_CD - 법정동 코드 10자리 중애서 앞 5자리 - guLawdCd; ex) 41135
 * 3. DEAL_YMD - 거래가 발생하 년월; ex) 202107
 *
 */
@Slf4j
@Component
public class ApartmentApiResource {

    @Value("${external.apartment-api.path}") // application,yml 에서 설정한 값 가져옴
    private String path;

    @Value("${external.apartment-api.service-key}") // application,yml 에서 설정한 값 가져옴
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
