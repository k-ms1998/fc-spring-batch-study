package com.fc.housebatch.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

    @XmlElement(name = "거래금액") // <item> 태그 안에 <amount> 태그의 값을 매핑
    private String dealAmount;

    @XmlElement(name = "건축년도") // <item> 태그 안에 <builtYear> 태그의 값을 매핑
    private Integer builtYear;

    @XmlElement(name = "년") // <item> 태그 안에 <year> 태그의 값을 매핑
    private Integer year;

    @XmlElement(name = "법정동") // <item> 태그 안에 <year> 태그의 값을 매핑
    private String dong;

    @XmlElement(name = "아파트") // <item> 태그 안에 <apt> 태그의 값을 매핑
    private String aptName;

    @XmlElement(name = "월") // <item> 태그 안에 <month> 태그의 값을 매핑
    private Integer month;

    @XmlElement(name = "일") // <item> 태그 안에 <day> 태그의 값을 매핑
    private Integer day;

    @XmlElement(name = "전용면적") // <item> 태그 안에 <area> 태그의 값을 매핑
    private Double area;

    @XmlElement(name = "지번") // <item> 태그 안에 <jibun> 태그의 값을 매핑
    private String jibun;

    @XmlElement(name = "지역코드") // <item> 태그 안에 <areaCode> 태그의 값을 매핑
    private String regionCode;

    @XmlElement(name = "층") // <item> 태그 안에 <floor> 태그의 값을 매핑
    private Integer floor;

    @XmlElement(name = "해제사유발생일") // <item> 태그 안에 <dealCanceledDate> 태그의 값을 매핑
    private String dealCanceledDate;

    @XmlElement(name = "해제여부") // <item> 태그 안에 <dealCanceled> 태그의 값을 매핑
    private String dealCanceled;

}
