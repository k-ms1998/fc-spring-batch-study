package com.fc.housebatch.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ����Ʈ �ǰŷ����� �ŷ� ������ ��� ��ü (apartment-api-response.xml ����)
 */
@ToString
@Getter
@Setter
@XmlRootElement(name = "item") // XML ������ <item> �±� Ȯ��
public class AptDealDto {

    @XmlElement(name = "�ŷ��ݾ�") // <item> �±� �ȿ� <�ŷ��ݾ�> �±��� ���� ����
    private String dealAmount;

    @XmlElement(name = "����⵵") // <item> �±� �ȿ� <����⵵> �±��� ���� ����
    private Integer builtYear;

    @XmlElement(name = "��") // <item> �±� �ȿ� <��> �±��� ���� ����
    private Integer year;

    @XmlElement(name = "��") // <item> �±� �ȿ� <��> �±��� ���� ����
    private String dong;

    @XmlElement(name = "����Ʈ") // <item> �±� �ȿ� <����Ʈ> �±��� ���� ����
    private String aptName;

    @XmlElement(name = "��") // <item> �±� �ȿ� <��> �±��� ���� ����
    private Integer month;

    @XmlElement(name = "��") // <item> �±� �ȿ� <��> �±��� ���� ����
    private Integer day;

    @XmlElement(name = "�������") // <item> �±� �ȿ� <�������> �±��� ���� ����
    private Double area;

    @XmlElement(name = "����") // <item> �±� �ȿ� <����> �±��� ���� ����
    private String jibun;

    @XmlElement(name = "�����ڵ�") // <item> �±� �ȿ� <�����ڵ�> �±��� ���� ����
    private String regionCode;

    @XmlElement(name = "��") // <item> �±� �ȿ� <��> �±��� ���� ����
    private Integer floor;

    @XmlElement(name = "���������߻���") // <item> �±� �ȿ� <���������߻���> �±��� ���� ����
    private String dealCanceledDate;

    @XmlElement(name = "��������") // <item> �±� �ȿ� <��������> �±��� ���� ����
    private String dealCanceled;

}
