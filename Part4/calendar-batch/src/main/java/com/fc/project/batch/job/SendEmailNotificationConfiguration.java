package com.fc.project.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SendEmailNotificationConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 4;

    @Bean
    public Job sendEmailNotificationJob(Step endEmailNotificationStep) {
        return jobBuilderFactory.get("endEmailNotificationJob")
                .start(endEmailNotificationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step sendEmailNotificationStep() {
        return stepBuilderFactory.get("endEmailNotificationStep")
                .chunk(CHUNK_SIZE)
                .build();
    }

}
