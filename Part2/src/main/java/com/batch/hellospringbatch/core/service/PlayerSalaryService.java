package com.batch.hellospringbatch.core.service;

import com.batch.hellospringbatch.core.domain.dto.PlayerDto;
import com.batch.hellospringbatch.core.domain.dto.PlayerSalaryDto;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlayerSalaryService {

    public PlayerSalaryDto calculateSalary(PlayerDto playerDto) {
        int salary = (Year.now().getValue() - playerDto.getBirthYear()) * 100;
        return PlayerSalaryDto.of(playerDto, salary);
    }
}
