package com.fc.housebatch.core.dto;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 아파트 실거래가의 거래 정보를 담는 객체 (apartment-api-response.xml 참고)
 *
 * @Setter 추가시 오류 발생 => @XmlElement 로 XML 을 객체 매핑할때 @Setter 와 충돌 발생
 */
@ToString
@Getter
@XmlRootElement(name = "item") // XML 파일의 <item> 태그 확인
@NoArgsConstructor
public class AptDealDto {

    @XmlElement(name = "거래금액") // <item> 태그 안에 <거래금액> 태그의 값을 매핑
    private String dealAmount;

    @XmlElement(name = "건축년도") // <item> 태그 안에 <건축년도> 태그의 값을 매핑
    private Integer builtYear;

    @XmlElement(name = "년") // <item> 태그 안에 <년> 태그의 값을 매핑
    private Integer year;

    @XmlElement(name = "법정동") // <item> 태그 안에 <법정동> 태그의 값을 매핑
    private String dong;

    @XmlElement(name = "아파트") // <item> 태그 안에 <아파트> 태그의 값을 매핑
    private String aptName;

    @XmlElement(name = "월") // <item> 태그 안에 <월> 태그의 값을 매핑
    private Integer month;

    @XmlElement(name = "일") // <item> 태그 안에 <일> 태그의 값을 매핑
    private Integer day;

    @XmlElement(name = "전용면적") // <item> 태그 안에 <전용면적> 태그의 값을 매핑
    private Double area;

    @XmlElement(name = "지번") // <item> 태그 안에 <지번> 태그의 값을 매핑
    private String jibun;

    @XmlElement(name = "지역코드") // <item> 태그 안에 <지역코드> 태그의 값을 매핑
    private String regionCode;

    @XmlElement(name = "층") // <item> 태그 안에 <층> 태그의 값을 매핑
    private Integer floor;

    @XmlElement(name = "해제사유발생일") // <item> 태그 안에 <해제사유발생일> 태그의 값을 매핑
    private String dealCanceledDate; // O

    @XmlElement(name = "해제여부") // <item> 태그 안에 <해제여부> 태그의 값을 매핑
    private String dealCanceled; // yy.MM.dd

    public LocalDate dealDateFromAptDealDto() {
        Integer dealYear = this.getYear();
        Integer dealMonth = this.getMonth();
        Integer dealDay = this.getDay();

        return LocalDate.of(dealYear, dealMonth, dealDay);
    }

    public Long dealAmountFromAptDealDto() {
        /**
         * dealAmount 의 ',' 를 공백으로 치환
         */
        String amount = this.dealAmount.replaceAll(",", "").trim();
        
        return Long.parseLong(amount);
    }

    public Boolean dealCanceledFromAptDealDto(){
        return (this.dealCanceled.equals("O"));
    }

    public LocalDate dealCanceledDateFromAptDealDto(){
        if (StringUtils.isBlank(this.dealCanceledDate)) {
            return null;
        }

        return LocalDate.parse(this.dealCanceledDate.trim(),
                DateTimeFormatter.ofPattern("yy.MM.dd"));
    }
}