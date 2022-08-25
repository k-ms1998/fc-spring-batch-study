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

    @XmlElement(name = "amount") // <item> 태그 안에 <amount> 태그의 값을 매핑
    private String dealAmount;

    @XmlElement(name = "builtYear") // <item> 태그 안에 <builtYear> 태그의 값을 매핑
    private Integer builtYear;

    @XmlElement(name = "year") // <item> 태그 안에 <year> 태그의 값을 매핑
    private Integer year;

    @XmlElement(name = "dong") // <item> 태그 안에 <year> 태그의 값을 매핑
    private String dong;

    @XmlElement(name = "apt") // <item> 태그 안에 <apt> 태그의 값을 매핑
    private String aptName;

    @XmlElement(name = "month") // <item> 태그 안에 <month> 태그의 값을 매핑
    private Integer month;

    @XmlElement(name = "day") // <item> 태그 안에 <day> 태그의 값을 매핑
    private Integer day;

    @XmlElement(name = "area") // <item> 태그 안에 <area> 태그의 값을 매핑
    private Double area;

    @XmlElement(name = "jibun") // <item> 태그 안에 <jibun> 태그의 값을 매핑
    private String jibun;

    @XmlElement(name = "areaCode") // <item> 태그 안에 <areaCode> 태그의 값을 매핑
    private String regionCode;

    @XmlElement(name = "floor") // <item> 태그 안에 <floor> 태그의 값을 매핑
    private Integer floor;

    @XmlElement(name = "dealCanceledDate") // <item> 태그 안에 <dealCanceledDate> 태그의 값을 매핑
    private String dealCanceledDate;

    @XmlElement(name = "dealCanceled") // <item> 태그 안에 <dealCanceled> 태그의 값을 매핑
    private String dealCanceled;

}
