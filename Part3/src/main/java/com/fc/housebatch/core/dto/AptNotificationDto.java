package com.fc.housebatch.core.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class AptNotificationDto {

    private String email;
    private String guName;
    private Integer count;
    private List<AptDto> aptDeals; // K - ����Ʈ �̸�, V - �ŷ� �ݾ�

    public String toMessage() {
        DecimalFormat decimalFormat = new DecimalFormat(); //decimalFormat.format(deal.getPrice()) => 130000 -> 130,000

        return String.format("%s ����Ʈ �ǰŷ��� �˸�\n" +
                "�� %d�� �ŷ��� �߻��߽��ϴ�.\n", guName, count) +
                aptDeals.stream()
                        .map(deal -> String.format("- %s : %s��", deal.getName(), decimalFormat.format(deal.getPrice())))
                        .collect(Collectors.joining());
    }

}

