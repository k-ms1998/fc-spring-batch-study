package com.batch.hellospringbatch.job.player;

import com.batch.hellospringbatch.core.domain.dto.PlayerDto;
import com.batch.hellospringbatch.core.domain.dto.PlayerSalaryDto;
import com.batch.hellospringbatch.core.service.PlayerSalaryService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
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
                .incrementer(new RunIdIncrementer())
                .start(flatFileStep)
                .build();
    }

    @Bean
    @JobScope
    public Step flatFileStep(FlatFileItemReader<PlayerDto> flatFileItemReader,
                             ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playerSalaryDtoItemProcessorAdapter) {
        return stepBuilderFactory
                .get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(flatFileItemReader)
                .processor(playerSalaryDtoItemProcessorAdapter)
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

    /**
     * PlayerSalaryService 를 받아와서, 해당 Service 에 있는 로직을 직접 메서드 명으로 호출해서 사용:
     * 이때, 메서드의 파라미터가 PlayerDto 이고 , return 값도 PlayerSalaryDto 로 일치해야 합니다.
     * (ItemProcessorAdapter<I, O> -> I = PlayerDto, O = PlayerSalaryDto)
     * 둘 중 하나라도 일치하지 않읗 경우 오류 발생
     */
    @Bean
    @StepScope
    public ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playerSalaryDtoItemProcessorAdapter(PlayerSalaryService playerSalaryService){
        ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(playerSalaryService);
        adapter.setTargetMethod("calculateSalary");
        /**
         * playerSalaryService.calculateService(PlayerDto) 호출
         */

        return adapter;
    }
}
