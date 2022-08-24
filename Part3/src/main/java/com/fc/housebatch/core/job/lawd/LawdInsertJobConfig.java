package com.fc.housebatch.core.job.lawd;

import com.fc.housebatch.core.entity.Lawd;
import com.fc.housebatch.core.job.validator.FilePathParameterValidator;
import com.fc.housebatch.core.service.LawdService;
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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

import static com.fc.housebatch.core.job.lawd.LawdFieldSetMapper.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LawdInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final LawdService lawdService;

    @Bean
    public Job lawdInsertJob(Step LawdInsertStep, FilePathParameterValidator filePathParameterValidator) {
        return jobBuilderFactory
                .get("lawdInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(filePathParameterValidator)
                .start(LawdInsertStep)
                .build();
    }

    @Bean
    @JobScope
    public Step lawdInsertStep(FlatFileItemReader<Lawd> flatFileLawdReader, ItemWriter<Lawd> lawdItemWriter) {
        /**
         * 법정동(LAWD) 파일을 읽어서 바로 DB로 저장하기 때문에 Reader, Writer 만 필요함(Processor X)
         */
        return stepBuilderFactory
                .get("lawdInsertStep")
                .<Lawd, Lawd>chunk(1000)
                .reader(flatFileLawdReader)
                .writer(lawdItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Lawd> flatFileLawdReader(@Value("#{jobParameters['filePath']}") String filePath){ // 파라미터 값으로 파일의 경로 받아오기

        return new FlatFileItemReaderBuilder<Lawd>()
                .name("flatFileLawdReader")
                .delimited()
                .delimiter("\t") // LAWD_CODE 는 탭(tab, \t) 으로 각 column 이 구별되어 있기 때문에, '\t' 으로 각 column을 나눔
                .names(LAWD_CD, LAWD_DONG, EXIST)/** delimiter 로 구분된 column 들의 이름을 원하는 대로 매핑 해줌
                                                    -> 구분된 이후, 첫번째 column 은 "lawdCd", 두번째는 "lawdDong", 세번째는 "exists" 로 매핑하기*/
                .linesToSkip(1) // 파일에서 첫번째 줄은 데이터가 아니므로 skip
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Lawd> lawdItemWriter() {
        return items -> {
            for (Lawd item : items) {
                lawdService.upsertLawd(item);
            }
        };
    }
}
