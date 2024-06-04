package com.example.apartmenttradedata.job.lawd;

import com.example.apartmenttradedata.core.entity.Lawd;
import com.example.apartmenttradedata.core.service.LawdService;
import com.example.apartmenttradedata.job.validator.FilePathParameterValidator;
import com.mysql.cj.log.Log;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;

import static com.example.apartmenttradedata.job.lawd.LawdFieldSetMapper.*;

@Configuration
@AllArgsConstructor
@Slf4j
public class LawdInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final LawdService lawdService;

    @Bean
    public Job lawdInsertJob(Step lawdInsertStep) {
        return  jobBuilderFactory.get("lawdInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathParameterValidator())
                .start(lawdInsertStep)
                .build();
    }

    @JobScope
    @Bean
    public Step lawdInsertStep(
            FlatFileItemReader<Lawd> lawdFlatFileItemReader,
            ItemWriter<Lawd> lawdItemWriter
    ) {
        return stepBuilderFactory.get("lawdInsertStep")
                .<Lawd, Lawd> chunk(1000)
                .reader(lawdFlatFileItemReader)
                .writer(lawdItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Lawd> lawdFileItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        String encoding = "x-windows-949";
        return new FlatFileItemReaderBuilder<Lawd>()
                .name("lawdFileItemReader")
                .encoding(encoding)
                .delimited()
                .delimiter("\t")
                .names(LAWD_CD, LAWD_DONG, EXIST) // FiledSetMapper에서 파일 각각의 필드를 객체의 필드와 Mapping!
                .linesToSkip(1)
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Lawd> lawdItemWriter() {
        return list -> {
            for (Lawd lawd : list) {
                log.info(lawd.getLawdDong());
                lawdService.upsert(lawd);
            }
        };
    }

}
