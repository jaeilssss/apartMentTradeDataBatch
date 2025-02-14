package com.example.apartmenttradedata.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class FilePathParameterValidator implements JobParametersValidator {

    private static final String FILE_PATH = "filePath";
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String filePath = jobParameters.getString("filePath");
        if(!StringUtils.hasText(filePath)) {
            throw new JobParametersInvalidException(FILE_PATH + "가 빈 문자열이거나 존재하지 않습니다.");
        }

        Resource resource = new ClassPathResource(filePath);
        if(!resource.exists()) {
            throw new JobParametersInvalidException(FILE_PATH + "가 Class path에 존재하지않습니다. 경로를 확인해주세요");
        }
    }


}
