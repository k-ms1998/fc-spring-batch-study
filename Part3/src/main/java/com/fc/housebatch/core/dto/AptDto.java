package com.fc.housebatch.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class AptDto {

    private String name;
    private Long price;

    public AptDto(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}
