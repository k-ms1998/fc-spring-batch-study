package com.batch.hellospringbatch.job.player;

import com.batch.hellospringbatch.core.domain.dto.PlayerDto;
import com.batch.hellospringbatch.core.domain.dto.PlayerSalaryDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * https://docs.spring.io/spring-batch/docs/current/reference/html/readersAndWriters.html
 */
@Configuration
@AllArgsConstructor
public class FlatFileJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * CSV 등 Flat File 을 반복문으로 한줄씩 읽어서 파싱 하는 방법보다 청크로 다뤄서 더 효율적으로 처리 가능
     */
    @Bean
    public Job flatFileJob(Step flatFileStep) {
        return jobBuilderFactory
                .get("flatFileJob")
                .start(flatFileStep)
                .build();
    }

    @Bean
    @JobScope
    public Step flatFileStep(FlatFileItemReader<PlayerDto> flatFileItemReader) {
        return stepBuilderFactory
                .get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(flatFileItemReader)
                .writer(new ItemWriter<PlayerSalaryDto>() {
                    @Override
                    public void write(List<? extends PlayerSalaryDto> items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<PlayerDto> flatFileItemReader() {
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("flatFileItemReader")
                .lineTokenizer(new DelimitedLineTokenizer())    // ',' 로 라인에서 각 테이터를 구분
                .linesToSkip(1) // 가장 위에서 1개의 줄 Skip
                .fieldSetMapper(new PlayerFieldSetMapper()) // 각 라인을 어떻게 객체로 맵핑할지 정해줌
                .resource(new FileSystemResource("player-list.txt"))
                .build();
    }
}
