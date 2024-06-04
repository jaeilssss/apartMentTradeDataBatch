package com.example.apartmenttradedata.job.apt;

import com.example.apartmenttradedata.BatchTestConfig;
import com.example.apartmenttradedata.adapter.ApartmentApiResource;
import com.example.apartmenttradedata.core.repository.LawdRepository;
import com.example.apartmenttradedata.core.service.AptDealService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptDealInsertJobConfig.class, BatchTestConfig.class})
public class AptDealInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private AptDealService aptDealService;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private ApartmentApiResource apartmentApiResource;

    @Test
    public void success() throws Exception{
        //given
        when(lawdRepository.findDistinctGuLawdCd()).thenReturn(Arrays.asList("11110"));
        when(apartmentApiResource.getResource(anyString(), any())).thenReturn(
                new ClassPathResource("test-api-resource.xml")
        );
        // when

        JobParameters jobParameters = new JobParameters(Maps.newHashMap("yearMonth", new JobParameter("2021-07")));

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        verify(aptDealService, times(1)).upsert(any());


    }

    @Test
    public void fail_whenYearMonthNotExist() throws Exception {
        //given
        when(lawdRepository.findDistinctGuLawdCd()).thenReturn(Arrays.asList("41135"));
        when(apartmentApiResource.getResource(anyString(), any())).thenReturn(
                new ClassPathResource("test-api-resource.xml")
        );

        //when
        Assertions.assertThrows(JobParametersInvalidException.class,
                () -> jobLauncherTestUtils.launchJob());

        //then
        verify(aptDealService, never()).upsert(any());
    }
}
