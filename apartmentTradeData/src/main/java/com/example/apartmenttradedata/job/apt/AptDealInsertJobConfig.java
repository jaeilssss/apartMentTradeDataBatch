package com.example.apartmenttradedata.job.apt;

import com.example.apartmenttradedata.adapter.ApartmentApiResource;
import com.example.apartmenttradedata.core.dto.AptDealDto;
import com.example.apartmenttradedata.core.repository.LawdRepository;
import com.example.apartmenttradedata.core.service.AptDealService;
import com.example.apartmenttradedata.job.validator.FilePathParameterValidator;
import com.example.apartmenttradedata.job.validator.LawdCdParameterValidator;
import com.example.apartmenttradedata.job.validator.YearMonthParameterValidator;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.lang.reflect.Array;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AptDealInsertJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ApartmentApiResource apartmentApiResource;

    @Bean
    public Job aptDealInsertJob(
            Step aptDealInsertStep,
            Step guLawdCdStep
    ) {
        return jobBuilderFactory.get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(new YearMonthParameterValidator())
                .start(guLawdCdStep)
                .on("CONTINUABLE").to(aptDealInsertStep).next(guLawdCdStep)
                .from(guLawdCdStep)
                .on("*").end()
                .end()
                .build();
    }

    // validator가 여러개 일 경우 CompositeJobParametersValidator를 이용한다!
/*    private JobParametersValidator aptDealJobParameterValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(
                ));
        // validator를 두개로 나눈 이후는 재활용성을 위해서
        // 한 곳에 나두면 재활용성이 좋지 않음
        return validator;
    }*/

/*    @JobScope
    @Bean
    public Step guLawdCdStep(Tasklet guLawdCdTasklet) {
        이렇게 하면 안 됌 !! 왜냐하면 이 Step은 JobScope 영역이기 때문에!
        return stepBuilderFactory.get("guLawdCdStep")
                .tasklet(((stepContribution, chunkContext) -> {
            System.out.println("[contextPrintStep] guLawdCd = " + guLawdCd);
            //
            return RepeatStatus.FINISHED;
        });)
                .build();
    }*/
    @JobScope
    @Bean
    public Step guLawdCdStep(Tasklet guLawdCdTasklet) {
        return stepBuilderFactory.get("guLawdCdStep")
                .tasklet(guLawdCdTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet guLawdCdTasklet(LawdRepository lawdRepository) {
        return new guLawdTasklet(lawdRepository);
    }

    @JobScope
    @Bean
    public Step aptDealInsertStep(
            StaxEventItemReader<AptDealDto> aptDealDtoStaxEventItemReader,
            ItemWriter<AptDealDto> aptDealDtoItemWriter
    ) {
        return stepBuilderFactory.get("aptDealInsertStep")
                .<AptDealDto,AptDealDto>chunk(10)
                .reader(aptDealDtoStaxEventItemReader)
                .writer(aptDealDtoItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd,
            Jaxb2Marshaller aptDealDtoMarshaller
    ) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartmentApiResource.getResource(guLawdCd, YearMonth.parse(yearMonth)))
                .addFragmentRootElements("item")
                .unmarshaller(aptDealDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptDealDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }

    @StepScope
    @Bean
    public ItemWriter<AptDealDto> aptDealItemWriter(AptDealService aptDealService) {
        return items -> {
            items.forEach(aptDealService::upsert);
            System.out.println("----------Write Complete-----------");
        };
    }
}
