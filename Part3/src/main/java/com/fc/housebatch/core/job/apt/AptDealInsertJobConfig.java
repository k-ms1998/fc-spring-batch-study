package com.fc.housebatch.core.job.apt;

import ch.qos.logback.core.joran.event.stax.StaxEvent;
import com.fc.housebatch.core.dto.AptDealDto;
import com.fc.housebatch.core.job.validator.FilePathParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AptDealInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptDealInsertJob(Step aptDealInsertStep, FilePathParameterValidator filePathParameterValidator) {
        return jobBuilderFactory
                .get("aptDealInsertJob")
                .validator(filePathParameterValidator)
                .incrementer(new RunIdIncrementer())
                .start(aptDealInsertStep)
                .build();
    }

    @Bean
    @JobScope
    public Step aptDealInsertStep(StaxEventItemReader<AptDealDto> aptDealResourceReader) {
        return stepBuilderFactory
                .get("aptDealInsertStep")
                .<AptDealDto, AptDealDto>chunk(10)
                .reader(aptDealResourceReader)
                .writer(new ItemWriter<AptDealDto>() {
                    @Override
                    public void write(List<? extends AptDealDto> items) throws Exception {
                        items.forEach(i -> System.out.println(i));
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(@Value("#{jobParameters['filePath']}") String filePath,
                                                                 Jaxb2Marshaller aptDealDtoMarshaller) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(new ClassPathResource(filePath))
                .addFragmentRootElements("item") // ���� Element ����; xml ���Ͽ��� <item> �±� �б�
                .unmarshaller(aptDealDtoMarshaller) // ����(apartment-api-response.xml)�� ��ü(AptDealDto)�� �����ϱ�
                .build();
    }

    @Bean
    @StepScope
    public Jaxb2Marshaller aptDealDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        /**
         * AptDealDto �� �����ϱ�
         */
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);

        return jaxb2Marshaller;
    }

}
