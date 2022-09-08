package com.fc.housebatch.core.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Configuration
public class DealDateParameterValidator implements JobParametersValidator {

    private static final String DEAL_DATE = "dealDate";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String dealDate = parameters.getString(DEAL_DATE);

        if (!StringUtils.hasText(dealDate)) {
            throw new JobParametersInvalidException(DEAL_DATE + "�� �� ���ڿ��̰ų� �������� �ʽ��ϴ�");
        }

        try {
            LocalDate.parse(dealDate);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(DEAL_DATE + "�� �ùٸ� ��¥ ������ �ƴմϴ�.(yyyy-MM-dd)");
        }

    }
}
