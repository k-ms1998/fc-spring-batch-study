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

    @XmlElement(name = "�ŷ��ݾ�") // <item> �±� �ȿ� <amount> �±��� ���� ����
    private String dealAmount;

    @XmlElement(name = "����⵵") // <item> �±� �ȿ� <builtYear> �±��� ���� ����
    private Integer builtYear;

    @XmlElement(name = "��") // <item> �±� �ȿ� <year> �±��� ���� ����
    private Integer year;

    @XmlElement(name = "������") // <item> �±� �ȿ� <year> �±��� ���� ����
    private String dong;

    @XmlElement(name = "����Ʈ") // <item> �±� �ȿ� <apt> �±��� ���� ����
    private String aptName;

    @XmlElement(name = "��") // <item> �±� �ȿ� <month> �±��� ���� ����
    private Integer month;

    @XmlElement(name = "��") // <item> �±� �ȿ� <day> �±��� ���� ����
    private Integer day;

    @XmlElement(name = "�������") // <item> �±� �ȿ� <area> �±��� ���� ����
    private Double area;

    @XmlElement(name = "����") // <item> �±� �ȿ� <jibun> �±��� ���� ����
    private String jibun;

    @XmlElement(name = "�����ڵ�") // <item> �±� �ȿ� <areaCode> �±��� ���� ����
    private String regionCode;

    @XmlElement(name = "��") // <item> �±� �ȿ� <floor> �±��� ���� ����
    private Integer floor;

    @XmlElement(name = "���������߻���") // <item> �±� �ȿ� <dealCanceledDate> �±��� ���� ����
    private String dealCanceledDate;

    @XmlElement(name = "��������") // <item> �±� �ȿ� <dealCanceled> �±��� ���� ����
    private String dealCanceled;

}
