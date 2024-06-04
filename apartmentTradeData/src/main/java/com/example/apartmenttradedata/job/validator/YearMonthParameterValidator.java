package com.example.apartmenttradedata.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.awt.print.Pageable;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class YearMonthParameterValidator implements JobParametersValidator {
    private static final String YEAR_MONTH = "yearMonth";
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String yearMonth = jobParameters.getString(YEAR_MONTH);
        if(!StringUtils.hasText(yearMonth)) {
            throw new JobParametersInvalidException(yearMonth + "가 빈 문자열 이거나 존재하지 않습니다.");
        }
        try {
            YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(yearMonth + "가 올바른 날짜 형식이 아닙니다. yyyy-MM 형식이어야 합니다.");

        }
    }
}
