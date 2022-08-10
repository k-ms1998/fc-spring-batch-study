package com.batch.hellospringbatch.core.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PlayerSalaryDto {

    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int salary;

    public PlayerSalaryDto(String ID, String lastName, String firstName, String position, int birthYear, int debutYear, int salary) {
        this.ID = ID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.birthYear = birthYear;
        this.debutYear = debutYear;
        this.salary = salary;
    }

    public static PlayerSalaryDto of(PlayerDto playerDto, int salary) {
        return new PlayerSalaryDto(playerDto.getID(), playerDto.getLastName(), playerDto.getFirstName(),
                playerDto.getPosition(), playerDto.getBirthYear(), playerDto.getDebutYear(), salary);
    }
}
