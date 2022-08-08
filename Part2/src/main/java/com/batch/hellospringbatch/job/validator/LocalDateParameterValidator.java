package com.batch.hellospringbatch.job.validator;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@AllArgsConstructor
public class LocalDateParameterValidator implements JobParametersValidator {

    private final String parameterName;

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String localDate = parameters.getString(parameterName);

        if (!StringUtils.hasText(localDate)) {
            throw new JobParametersInvalidException(parameterName + " is empty.");
        }

        try {
            LocalDate parsedDate = LocalDate.parse(localDate);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(parameterName + " is not formatted correctly.");
        }
    }
}
