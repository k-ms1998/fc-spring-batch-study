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
    private List<AptDto> aptDeals; // K - 아파트 이름, V - 거래 금액

    public String toMessage() {
        DecimalFormat decimalFormat = new DecimalFormat(); //decimalFormat.format(deal.getPrice()) => 130000 -> 130,000

        return String.format("%s 아파트 실거래가 알림\n" +
                "총 %d개 거래가 발생했습니다.\n", guName, count) +
                aptDeals.stream()
                        .map(deal -> String.format("- %s : %s원", deal.getName(), decimalFormat.format(deal.getPrice())))
                        .collect(Collectors.joining());
    }

}

