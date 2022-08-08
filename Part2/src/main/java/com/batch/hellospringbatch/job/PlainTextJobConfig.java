package com.batch.hellospringbatch.job;

import com.batch.hellospringbatch.core.domain.PlainText;
import com.batch.hellospringbatch.core.domain.ResultText;
import com.batch.hellospringbatch.core.repository.PlainTextRepository;
import com.batch.hellospringbatch.core.repository.ResultTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PlainTextJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlainTextRepository plainTextRepository;

    @Bean("plainTextJob")
    public Job plainTextJob(Step plainTextStep) {
        return jobBuilderFactory
                .get("plainTextJob")
                .incrementer(new RunIdIncrementer())
                .start(plainTextStep)
                .build();
    }

    @Bean("plainTextStep")
    @JobScope // Step 을 호출한 Job 에서만 살아 있도록 설정
    public Step plainTextStep(RepositoryItemReader<PlainText> plainTextItemReader, ItemProcessor<PlainText, ResultText> plainTextItemProcessor,
                              ItemWriter<ResultText> plainTextItemWriter) {
        return stepBuilderFactory
                .get("plainTextStep")
                .<PlainText, ResultText>chunk(5)
                .reader(plainTextItemReader)
                .processor(plainTextItemProcessor)
                .writer(plainTextItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<PlainText> plainTextItemReader() {
        return new RepositoryItemReaderBuilder<PlainText>()
                .name("plainTextItemReader")
                .repository(plainTextRepository)
                .methodName("findBy")
                .pageSize(5)
                .arguments(List.of()) // Repository 의 메소드에 파라미터를 넘겨주는데 사용; 리스트 형태로 파라미터를 넘겨줌
                .sorts(Collections.singletonMap("id", Sort.Direction.DESC)) // 'id' 를 내림차순 순서로 정렬
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PlainText, ResultText> plainTextItemProcessor() {
        return item -> {
            return new ResultText("Processed: " + item.getText());
        };
        /*
        == return new ItemProcessor<PlainText, String>() {
            @Override
            public String process(PlainText item) throws Exception {
                return null;
            }
        };*/
    }

    @Bean
    @StepScope
    public ItemWriter<ResultText> plainTextItemWriter(ResultTextRepository resultTextRepository) {
        return items -> {
            items.forEach(item -> {
                ResultText save = resultTextRepository.save(item);
                System.out.println(save.getText());
            });
            System.out.println("===== chunk is finished =====");
        };
        /*
        == return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {

            }
        };*/
    }

}
