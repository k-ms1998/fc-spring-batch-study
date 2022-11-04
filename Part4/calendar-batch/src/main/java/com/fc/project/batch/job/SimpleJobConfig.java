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

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob(Step simpleStep) {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep() {
        return stepBuilderFactory.get("simpleStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("----- [Simple Job] -----");

                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

}
