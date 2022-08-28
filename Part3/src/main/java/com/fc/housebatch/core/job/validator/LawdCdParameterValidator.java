package com.fc.housebatch.core.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class LawdCdParameterValidator implements JobParametersValidator {

    private static final String LAWD_CD = "lawdCd";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String lawdCd = parameters.getString(LAWD_CD);

        if (isValidLawdCd(lawdCd)) {
            throw new JobParametersInvalidException(LAWD_CD + " 은 다섯 자리여야 합니다.");
        }

    }

    private boolean isValidLawdCd(String lawdCd) {
        return !(StringUtils.hasText(lawdCd) && lawdCd.length() == 5);
    }
}
