package com.batch.hellospringbatch.job.parallel;

import com.batch.hellospringbatch.core.domain.dto.AmountDto;
import lombok.AllArgsConstructor;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.io.IOException;

/**
 * 단일 프로세스에서 청크 단위로 병렬 처리
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class MultiThreadStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiThreadJob(Step multiThreadStep) {
        return jobBuilderFactory
                .get("multiThreadJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadStep)
                .build();

    }

    @Bean
    @JobScope
    public Step multiThreadStep(FlatFileItemReader<AmountDto> flatFileAmountItemReader,
                                ItemProcessor<AmountDto, AmountDto> amountItemProcessor,
                                FlatFileItemWriter<AmountDto> flatFileAmountItemWriter,
                                TaskExecutor taskExecutor) {

        /**
         * Multi Threading 사용 시, 하나의 thread 가 chunkSize 만큼 처리함
         * -> 즉, chunkSize 가 10이면, 여러개의 thread 가 10개의 데이터 씩 동시에 읽고 처리함
         */
        return stepBuilderFactory
                .get("multiThreadStep")
                .<AmountDto, AmountDto>chunk(10)
                .reader(flatFileAmountItemReader)
                .processor(amountItemProcessor)
                .writer(flatFileAmountItemWriter)
                .taskExecutor(taskExecutor) // Enabling Multi Threading
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring-batch-task-executor");

        return taskExecutor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<AmountDto> flatFileAmountItemReader() {
        return new FlatFileItemReaderBuilder<AmountDto>()
                .name("flatFileAmountItemReader")
                .fieldSetMapper(new AmountFieldSetMapper())
                .lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB))
                .resource(new FileSystemResource("data/input.txt"))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AmountDto, AmountDto> amountItemProcessor() {
        return item -> {
            item.setAmount(item.getAmount() * 100);
            /**
             * 현재 쓰레드의 이름을 출력해보면 여러개의 쓰레드가 존재하는 것을 알 수 있음
             * && 하나의 thread 가 chunkSize 만큼의 데이터를 처리하는 것을 볼 수 있음
             */
            log.info("[Simple Multi Thread Batch] " + Thread.currentThread().getName() + " , item = " + item);
            return item;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<AmountDto> flatFileAmountItemWriter() throws IOException {
        BeanWrapperFieldExtractor<AmountDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"index", "name", "amount"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<AmountDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setFieldExtractor(fieldExtractor);

        String outputFilePath = "data/output.txt";
        new File(outputFilePath).createNewFile();

        return new FlatFileItemWriterBuilder<AmountDto>()
                .name("flatFileAmountItemWriter")
                .resource(new FileSystemResource("data/output.txt"))
                .lineAggregator(lineAggregator)
                .build();
    }
}
