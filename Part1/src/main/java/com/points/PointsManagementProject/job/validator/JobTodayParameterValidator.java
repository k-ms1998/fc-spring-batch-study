package com.points.PointsManagementProject.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class JobTodayParameterValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        /**
         * 1. parameters 가 null 인지 검사
         */
        if (parameters.equals(null)) {
            throw new JobParametersInvalidException("Job Parameter is NULL");
        }

        /**
         * 2. parameters 에서 today 파라미터가 null 인지 검사
         */
        String todayStr = parameters.getString("today");
        if (todayStr.equals(null)) {
            throw new JobParametersInvalidException("Job Parameter is NULL");
        }

        /**
         * 3. today 파라미터 파싱 시도 -> 실패 시, 날짜 형태가 아닌 상태
         */
        try {
            LocalDate.parse(todayStr);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException("Job Parameter 'today' is not formatted correctly");
        }
    }
}
