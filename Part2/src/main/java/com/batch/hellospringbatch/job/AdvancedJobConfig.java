package com.batch.hellospringbatch.job;

import com.batch.hellospringbatch.job.validator.LocalDateParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdvancedJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("advancedJob")
    public Job advancedJob(Step advancedStep) {
        return jobBuilderFactory
                .get("advancedJob")
                .validator(new LocalDateParameterValidator("targetDate")) // 'targetDate' 가 null 인지 && 올바른 형식인지 validate
                .incrementer(new RunIdIncrementer())
                .start(advancedStep)
                .build();
    }

    @Bean
    @JobScope
    public Step advancedStep(Tasklet advancedTasklet) {
        return stepBuilderFactory
                .get("advancedStep")
                .tasklet(advancedTasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet advancedTasklet(@Value("#{jobParameters['targetDate']}") String targetDate) {
        return (contribution, chunkContext) -> {
            /**
             * Validator vs. No Validator:
             * 만약에 targetDate 가 YYYY-MM-DD 형식의 String이 아닐때:
             * -> Validator: Job 에 지정해준 validator (LocalDateParameterValidator) 에서  'targetDate' 가 올바른 형식인지 Job 이 실행하기 전에 검사
             *  => 올바른 형식이 아니면 Job 이 실행되기 전에 throws Exception
             * -> No Validator : Tasklet 에서 LocalDate.parse(targetDate) 를 실행할때 오류 발생
             *  => 올바른 형식이 아니면 Job 실행 도중에 오류 발생 => 불안정성 증가 (Because, 오류 발생 전에 데이터를 수정하는 작업을 수행했을 수도 있음)
             */
            LocalDate parsedDate = LocalDate.parse(targetDate);
            log.info("[Advanced Job] Job Parameter : targetDate = " + targetDate);
            log.info("[Advanced Job] Executed Tasklet");
            return RepeatStatus.FINISHED;
        };
    }

}
