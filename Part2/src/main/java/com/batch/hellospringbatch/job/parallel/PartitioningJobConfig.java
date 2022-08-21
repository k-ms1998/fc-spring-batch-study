package com.batch.hellospringbatch.job.parallel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * 단일 프로세스에서 Master Step 과 Worker Step 을 두고, Master Step 에서 생성한 파티션 단위로 Step 을 병렬 처리
 *
 * Master Step 이 지정된 수의 Worker Step 으로 일감을 분할 처리하는 것
 * 이때, 각각 Worker Step 은 각각의 Reader, Processor, Writer 등을 가지고 동작할 수 있고 세밀하고 동적으로 설정을 해줄수 있음
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class PartitioningJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final int PARTITION_SIZE = 100;

    @Bean
    public Job partitioningJob(Step masterStep) {
        return jobBuilderFactory
                .get("partitioningJob2")
                .start(masterStep)
                .build();
    }

    @Bean
    @JobScope
    public Step masterStep( Partitioner partitioner, TaskExecutorPartitionHandler partitionHandler) {
        return stepBuilderFactory
                .get("masterStep")
                .partitioner("anotherStep", partitioner) // (처리할 Step 이름, Partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    /**
     * girdSize(PARTITION_SIZE) 만큼의 StepExecution 과 Execution Context 실행 
     * (Execution Context 에는 파티셔닝할 데이터 정보가 들어감. 실제 데이터가 아니라 탐색할 데이터의 시작과 끝 지점의 정보가 들어감)
     * girdSize(PARTITION_SIZE) 만큼의 Task 및 Thread 생성
     * -> Thread 별로 독립적으로 StepExecution 을 실행
     * @return
     */
    @Bean
    @StepScope
    public Partitioner partitioner() {
        SimplePartitioner partitioner = new SimplePartitioner();
        partitioner.partition(PARTITION_SIZE);

        return partitioner;
    }

    /**
     * @param anotherStep : ParallelStepJobConfig 에서 빈 등록한 Step 을 주입 받아서 재사용
     * @param taskExecutor : MultiThreadStepJobConfig 에서 빈 등록한 TaskExecutor 을 주입 받아서 재사용
     * @return
     */
    @Bean
    @StepScope
    public TaskExecutorPartitionHandler partitionHandler(Step anotherStep, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(anotherStep);
        partitionHandler.setGridSize(PARTITION_SIZE);
        partitionHandler.setTaskExecutor(taskExecutor);

        return partitionHandler;
    }
}
