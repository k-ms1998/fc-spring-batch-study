package com.fc.housebatch.core.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class YearMonthParameterValidator implements JobParametersValidator {

    private static final String YEAR_MONTH = "yearMonth";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String yearMonthStr = parameters.getString(YEAR_MONTH);

        if (!StringUtils.hasText(yearMonthStr)) {
            throw new JobParametersInvalidException(YEAR_MONTH + " parameter is NULL.");
        }

        try {
            YearMonth.parse(yearMonthStr);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(YEAR_MONTH + " 가 올바른 날짜 형식이 아닙니다. yyyy-MM 형식으로 입력해 주세요.");
        }

    }
}
