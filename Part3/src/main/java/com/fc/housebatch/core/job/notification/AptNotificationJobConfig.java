package com.fc.housebatch.core.job.notification;

import com.fc.housebatch.core.dto.AptNotificationDto;
import com.fc.housebatch.core.entity.AptNotification;
import com.fc.housebatch.core.job.validator.DealDateParameterValidator;
import com.fc.housebatch.core.repository.AptNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AptNotificationJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptNotificationJob(Step aptNotificationStep, DealDateParameterValidator dealDateParameterValidator) {
        return jobBuilderFactory
                .get("aptNotificationJob")
                .incrementer(new RunIdIncrementer())
                .validator(dealDateParameterValidator)
                .start(aptNotificationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step aptNotificationStep(RepositoryItemReader<AptNotification> aptNotificationItemReader,
                                    ItemProcessor<AptNotification, AptNotificationDto> aptNotificationItemProcessor,
                                    ItemWriter<AptNotificationDto> aptNotificationItemWriter) {
        return stepBuilderFactory
                .get("aptNotificationStep")
                .<AptNotification, AptNotificationDto>chunk(10)
                .reader(aptNotificationItemReader)
                .processor(aptNotificationItemProcessor)
                .writer(aptNotificationItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<AptNotification> aptNotificationItemReader(AptNotificationRepository aptNotificationRepository) {
        return new RepositoryItemReaderBuilder<AptNotification>()
                .name("aptNotificationItemReader")
                .repository(aptNotificationRepository)
                .methodName("findByEnabledIsTrue")
                .pageSize(10)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("aptNotificationId", Sort.Direction.DESC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AptNotification, AptNotificationDto> aptNotificationItemProcessor() {
        return new ItemProcessor<AptNotification, AptNotificationDto>() {
            @Override
            public AptNotificationDto process(AptNotification item) throws Exception {
                return null;
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AptNotificationDto> aptNotificationItemWriter() {
        return items -> {

        };
    }


}

