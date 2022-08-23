package com.fc.housebatch.core.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * 파일의 위치를 확인하는 Validator
 */
@Configuration
public class FilePathParameterValidator implements JobParametersValidator {

    private static final String FILE_PATH = "filePath";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        /**
         * 파라미터 중에서 이름이 filePath 인 파라미터의 값을 가져옴
         */
        String filePath = parameters.getString(FILE_PATH);

        /**
         * 파라미터 값이 없을때
         */
        if (!StringUtils.hasText(filePath)) {
            throw new JobParametersInvalidException(FILE_PATH + " 빈 문자열 또는 존재하지 않습니다.");
        }

        /**
         * 프로젝트 안에 파라미터로 넘겨 받은 이름의 파일이 없을때
         */
        Resource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            throw new JobParametersInvalidException(FILE_PATH + " class path에 존재하지 않습니다. 경로를 확인해 주세요.");
        }

        
    }
}
