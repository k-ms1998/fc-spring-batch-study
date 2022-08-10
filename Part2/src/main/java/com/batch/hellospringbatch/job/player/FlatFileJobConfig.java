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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
                             ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playerSalaryDtoItemProcessorAdapter,
                             FlatFileItemWriter<PlayerSalaryDto> playerSalaryDtoFlatFileItemWriter) {
        return stepBuilderFactory
                .get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(flatFileItemReader)
                .processor(playerSalaryDtoItemProcessorAdapter)
                .writer(playerSalaryDtoFlatFileItemWriter)
                .allowStartIfComplete(true) // BATCH_JOB_EXECUTION 테이블에 동일한 JOB_INSTANCE_ID 를 가진 튜플에 status 가 COMPLETED 인 경우 Step 재실행 X 
                                                // -> 'Step already complete or not restartable, so no action to execute'
                                                // 그러므로, 1. Parameter 을 항상 새로 설정(ex: LocalDateTime.now()) or 2. allowStartIfComplete(true) 추가
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

    @Bean
    @StepScope
    public FlatFileItemWriter<PlayerSalaryDto> playerSalaryDtoFlatFileItemWriter() throws IOException {
        /**
         * Entity 에서 Write 하고 싶은 필드명들 명시하기
         */
        BeanWrapperFieldExtractor<PlayerSalaryDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "firstName", "lastName", "salary"}); // PlayerSalaryDto 중에서 파일에 Write 할 필드 명들을 명시 해줌 -> 명시한 순서대로 Write 됨
        fieldExtractor.afterPropertiesSet();

        /**
         * 데이터를 어떻게 조합할지 정해주기
         * ex: 위의 fieldExtractor 에서 명시해준 필드 순서대로 하면 : ID|firstName|lastName|salary 가 Write 됨
         */
        DelimitedLineAggregator<PlayerSalaryDto> delimitedLineAggregator = new DelimitedLineAggregator<>();
        delimitedLineAggregator.setDelimiter("|"); // 필드 간 구분을 어떻게 할지 명시 -> field1|field2|field3|....
        delimitedLineAggregator.setFieldExtractor(fieldExtractor);

        new File("player-salary-list.txt").createNewFile(); // createNexFile() -> Job 이 실행 될때마다 파일을 새로 만들어서 Write -> 전에 Write 했던 데이터들 사라짐

        return new FlatFileItemWriterBuilder<PlayerSalaryDto>()
                .name("playerSalaryDtoFlatFileItemWriter")
                .resource(new FileSystemResource("player-salary-list.txt")) // 파일을 Write 할 장소 -> player-salary-list.txt
                .lineAggregator(delimitedLineAggregator) // Write 할 데이터를 어떻게 조합할지 설정
                .build();
    }
}
