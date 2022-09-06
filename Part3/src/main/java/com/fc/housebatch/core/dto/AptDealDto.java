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
import java.util.Optional;

/**
 * 아파트 실거래가의 거래 정보를 담는 객체 (apartment-api-response.xml 참고)
 *
 * @Setter 추가시 오류 발생 => @XmlElement 로 XML 을 객체 매핑할때 @Setter 와 충돌 발생
 */
@ToString
@XmlRootElement(name = "item") // XML 파일의 <item> 태그 확인
@NoArgsConstructor
public class AptDealDto {

    @XmlElement(name = "DealAmount") // <item> 태그 안에 <DealAmount> 태그의 값을 매핑
    private String dealAmount;

    @XmlElement(name = "BuiltYear") // <item> 태그 안에 <BuiltYear> 태그의 값을 매핑
    private Integer builtYear;

    @XmlElement(name = "Year") // <item> 태그 안에 <Year> 태그의 값을 매핑
    private Integer year;

    @XmlElement(name = "Dong") // <item> 태그 안에 <Dong> 태그의 값을 매핑
    private String dong;

    @XmlElement(name = "AptName") // <item> 태그 안에 <AptName> 태그의 값을 매핑
    private String aptName;

    @XmlElement(name = "Month") // <item> 태그 안에 <Month> 태그의 값을 매핑
    private Integer month;

    @XmlElement(name = "Day") // <item> 태그 안에 <Day> 태그의 값을 매핑
    private Integer day;

    @XmlElement(name = "ExclusiveArea") // <item> 태그 안에 <ExclusiveArea> 태그의 값을 매핑
    private Double area;

    @XmlElement(name = "Jibun") // <item> 태그 안에 <Jibun> 태그의 값을 매핑
    private String jibun;

    @XmlElement(name = "AreaCode") // <item> 태그 안에 <AreaCode> 태그의 값을 매핑
    private String regionCode;

    @XmlElement(name = "Floor") // <item> 태그 안에 <Floor> 태그의 값을 매핑
    private Integer floor;

    @XmlElement(name = "DealCanceledDate") // <item> 태그 안에 <DealCanceledDate> 태그의 값을 매핑
    private String dealCanceledDate; // O

    @XmlElement(name = "DealCanceled") // <item> 태그 안에 <DealCanceled> 태그의 값을 매핑
    private String dealCanceled; // yy.MM.dd

    public String getDealAmount() {
        return dealAmount;
    }

    public Integer getBuiltYear() {
        return Optional.ofNullable(this.builtYear).orElse(0);
    }

    public Integer getYear() {
        return Optional.ofNullable(this.year).orElse(0);
    }

    public String getDong() {
        return Optional.ofNullable(this.dong).orElse("");
    }

    public String getAptName() {
        return Optional.ofNullable(this.aptName).orElse("");
    }

    public Integer getMonth() {
        return Optional.ofNullable(this.month).orElse(0);
    }

    public Integer getDay() {
        return Optional.ofNullable(this.day).orElse(0);
    }

    public Double getArea() {
        return Optional.ofNullable(this.area).orElse(0.0);
    }

    public String getJibun() {
        return Optional.ofNullable(this.jibun).orElse("");
    }

    public String getRegionCode() {
        return Optional.ofNullable(this.regionCode).orElse("");
    }

    public Integer getFloor() {
        return Optional.ofNullable(this.floor).orElse(0);
    }

    public String getDealCanceledDate() {
        return Optional.ofNullable(this.dealCanceledDate).orElse("");
    }

    public String getDealCanceled() {
        return Optional.ofNullable(this.dealCanceled).orElse("");
    }

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