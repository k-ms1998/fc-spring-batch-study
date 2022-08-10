package com.batch.hellospringbatch.job;

import com.batch.hellospringbatch.job.validator.LocalDateParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
    public Job advancedJob(Step advancedStep, JobExecutionListener advancedJobExecutionListener) {
        return jobBuilderFactory
                .get("advancedJob")
                .validator(new LocalDateParameterValidator("targetDate")) // 'targetDate' 가 null 인지 && 올바른 형식인지 validate
                .listener(advancedJobExecutionListener)
                .incrementer(new RunIdIncrementer())
                .start(advancedStep)
                .build();
    }

    @Bean
    @JobScope
    public JobExecutionListener advancedJobExecutionListener() {
        return new JobExecutionListener() {
            /**
             * Job 이 실행되지 전의 상태
             *
             * @param jobExecution
             */
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("[JobExecutionListener#beforeJob : Advanced Job] JobExecution is " + jobExecution.getStatus());
            }

            /**
             * Job 이 실행된 이후의 상태
             * Job 이 실행된 이후, 상태에 따라서 구현하고 싶은 로직이 있을때 afterJob 에서 구현
             *
             * ex) 만약에 Job 이 실패해서 관리자한테 알림을 보내고 싶은 경우:
             *      -> Listener 의 afterJob 에서 로직 구현
             *
             * @param jobExecution
             */
            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("[JobExecutionListener#beforeJob : Advanced Job] JobExecution is " + jobExecution.getStatus());
                if(jobExecution.getStatus() == BatchStatus.FAILED){
                    log.error("[JobExecutionListener#beforeJob : Advanced Job ERROR] !! JobExecution FAILED !!");
                }
            }
        };
    }

    @Bean
    @JobScope
    public Step advancedStep(StepExecutionListener stepExecutionListener, Tasklet advancedTasklet) {
        return stepBuilderFactory
                .get("advancedStep")
                .listener(stepExecutionListener)
                .tasklet(advancedTasklet)
                .build();
    }

    /**
     * [StepExecutionListener #BeforeStep] StepExecution is : STARTED
     * [Advanced Job] Job Parameter : targetDate = 2025-01-01
     * [Advanced Job] Executed Tasklet
     * [StepExecutionListener #AfterStep] StepExecution is : COMPLETED
     * @return
     *
     * StepListener 를 통해 Step 실행 전 또는 후에 로직을 수행하고 싶을 경우에 사용
     * */
    @Bean
    @StepScope
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            /**
             * Step 실행 전의 상태
             * @param stepExecution
             */
            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("[StepExecutionListener #BeforeStep] StepExecution is : " + stepExecution.getStatus());
            }

            /**
             * Step 실행 이후의 상태
             * @param stepExecution
             * @return
             */
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("[StepExecutionListener #AfterStep] StepExecution is : " + stepExecution.getStatus());
                return stepExecution.getExitStatus();
            }
        };
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

            /**
             * 만약에 targetDate 는 무조건 오늘보다 나중인 날짜이어야 될때:
             * targetDate 가 오늘보다 빠른 날짜이면 Exception
             *  -> JobExecution 의 status == Failed
             *      => Job Listener 의 afterJob 에서 Failed 일때의 로직 처리
             *          => log.error("[JobExecutionListener#beforeJob : Advanced Job ERROR] !! JobExecution FAILED !!");
             *              => '[JobExecutionListener#beforeJob : Advanced Job] !! JobExecution FAILED !!'
             */
            if (parsedDate.isBefore(LocalDate.now())) {
                throw new RuntimeException();
            }
            return RepeatStatus.FINISHED;
        };
    }

}
