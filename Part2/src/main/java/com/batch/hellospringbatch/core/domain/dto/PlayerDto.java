package com.batch.hellospringbatch.core.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PlayerDto {

    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;

    public PlayerDto(String ID, String lastName, String firstName, String position, int birthYear, int debutYear) {
        this.ID = ID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.birthYear = birthYear;
        this.debutYear = debutYear;
    }
}
