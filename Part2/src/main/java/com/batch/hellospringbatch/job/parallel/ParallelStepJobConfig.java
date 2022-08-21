package com.batch.hellospringbatch.job.parallel;

import com.batch.hellospringbatch.core.domain.dto.AmountDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * 단일 프로세스에서  Flow 를 이용해서 여러개의 Step 을 동시에 실행하기
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class ParallelStepJobConfig {
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job parallelJob(Flow splitFlow) {
        return jobBuilderFactory
                .get("parallelJob")
                .incrementer(new RunIdIncrementer())
                .start(splitFlow).build()
                .build();
    }

    /**
     * (MultiThreadStepJobConfig 에서 생성한하고 Bean으 로 등록한 taskExecutor 를 주입받아서 재사용하기)
     *
     * 동시에 두가지 이상의 Step을 실행하기
     * ex: 한 번에 flowAmountFileStep 과 flowAnotherStep 실행하기
     * 이때, 하나의 thread 가 각각 flowAmountFileStep 과 flowAnotherStep 을 처리함 
     * -> 그러므로, 총 두개의 thread 사용됨
     * @param taskExecutor
     * @return
     */
    @Bean
    public Flow splitFlow(TaskExecutor taskExecutor, Flow flowAmountFileStep, Flow flowAnotherStep) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor)
                .add(flowAmountFileStep, flowAnotherStep)
                .build();
    }

    @Bean
    public Flow flowAmountFileStep(Step amountFileStep) {
        return new FlowBuilder<SimpleFlow>("flowAmountFileStep")
                .start(amountFileStep)
                .build();
    }

    @Bean
    public Flow flowAnotherStep(Step anotherStep) {
        return new FlowBuilder<SimpleFlow>("flowAnotherStep")
                .start(anotherStep)
                .build();
    }

    /**
     * MultiThreadStepJobConfig 에서 생성한하고 Bean 으로 등록한 reader, writer, processor 재사용
     */
    @Bean
    public Step amountFileStep(FlatFileItemReader<AmountDto> flatFileAmountItemReader,
                               ItemProcessor<AmountDto, AmountDto> amountItemProcessor,
                               FlatFileItemWriter<AmountDto> flatFileAmountItemWriter) {

        return stepBuilderFactory
                .get("amountFileStep")
                .<AmountDto, AmountDto>chunk(10)
                .reader(flatFileAmountItemReader)
                .processor(amountItemProcessor)
                .writer(flatFileAmountItemWriter)
                .build();
    }

    @Bean
    public Step anotherStep() {
        return stepBuilderFactory
                .get("anotherStep")
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(500);
                    log.info("[Another Step] Thread = " + Thread.currentThread().getName());

                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
