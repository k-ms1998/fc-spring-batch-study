package com.batch.hellospringbatch.core.service;

import com.batch.hellospringbatch.core.domain.dto.PlayerDto;
import com.batch.hellospringbatch.core.domain.dto.PlayerSalaryDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerSalaryServiceTest {

    private PlayerSalaryService playerSalaryService;

    @BeforeEach
    public void setup() {
        playerSalaryService = new PlayerSalaryService();
    }

    @Test
    void given_whenExecutingCalculateSalary_then() {
        // Given
        /**
         * 테스트는 언제나 결과가 동일해야됨
         * 그러므로, 해당 테스트를 몇년 후에 실행하더라도 동일한 결과를 반환해야됨
         * But, PlayerSalaryDto.calculateSalary() 에서는 올해 년도(Year.now())를 가져와서 값을 구하는데, 해가 지나면 반환 값이 달라짐
         * 그러므로, mockStatic 을 이용해서 설정해줌 Year.now() 를 고정시켜줌
         */
        MockedStatic<Year> mockYearClass = mockStatic(Year.class);
        Year mockYear = mock(Year.class);

        /**
         * mockYear.getValue() 가 호출되면, 2020 반환
         */
        when(mockYear.getValue()).thenReturn(2020);

        /**
         * Year.now() 가호출될때 mockYear 를 반환
         */
        mockYearClass.when(()->Year.now()).thenReturn(mockYear);

        PlayerDto mockPlayer = mock(PlayerDto.class);
        /**
         * mockPlayer.getBirthYear() 가 호출되면 1980 반환
         */
        when(mockPlayer.getBirthYear()).thenReturn(1980);

        // When
        PlayerSalaryDto result = playerSalaryService.calculateSalary(mockPlayer);

        // Then
        Assertions.assertThat(result.getSalary()).isEqualTo(4000);

    }

}