package com.batch.hellospringbatch.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AmountDto {

    private int index;
    private String name;
    private int amount;

}
