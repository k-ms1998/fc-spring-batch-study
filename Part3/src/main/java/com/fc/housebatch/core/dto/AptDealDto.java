package com.fc.housebatch.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ����Ʈ �ǰŷ����� �ŷ� ������ ��� ��ü (apartment-api-response.xml ����)
 *
 * @Setter �߰��� ���� �߻� => @XmlElement �� XML �� ��ü �����Ҷ� @Setter �� �浹 �߻�
 */
@ToString
@Getter
@XmlRootElement(name = "item") // XML ������ <item> �±� Ȯ��
@NoArgsConstructor
public class AptDealDto {

    @XmlElement(name = "amount") // <item> �±� �ȿ� <amount> �±��� ���� ����
    private String dealAmount;

    @XmlElement(name = "builtYear") // <item> �±� �ȿ� <builtYear> �±��� ���� ����
    private Integer builtYear;

    @XmlElement(name = "year") // <item> �±� �ȿ� <year> �±��� ���� ����
    private Integer year;

    @XmlElement(name = "dong") // <item> �±� �ȿ� <year> �±��� ���� ����
    private String dong;

    @XmlElement(name = "apt") // <item> �±� �ȿ� <apt> �±��� ���� ����
    private String aptName;

    @XmlElement(name = "month") // <item> �±� �ȿ� <month> �±��� ���� ����
    private Integer month;

    @XmlElement(name = "day") // <item> �±� �ȿ� <day> �±��� ���� ����
    private Integer day;

    @XmlElement(name = "area") // <item> �±� �ȿ� <area> �±��� ���� ����
    private Double area;

    @XmlElement(name = "jibun") // <item> �±� �ȿ� <jibun> �±��� ���� ����
    private String jibun;

    @XmlElement(name = "areaCode") // <item> �±� �ȿ� <areaCode> �±��� ���� ����
    private String regionCode;

    @XmlElement(name = "floor") // <item> �±� �ȿ� <floor> �±��� ���� ����
    private Integer floor;

    @XmlElement(name = "dealCanceledDate") // <item> �±� �ȿ� <dealCanceledDate> �±��� ���� ����
    private String dealCanceledDate;

    @XmlElement(name = "dealCanceled") // <item> �±� �ȿ� <dealCanceled> �±��� ���� ����
    private String dealCanceled;

}
