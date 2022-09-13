package com.fc.housebatch.core.job.notification;

import com.fc.housebatch.core.dto.AptDto;
import com.fc.housebatch.core.dto.AptNotificationDto;
import com.fc.housebatch.core.entity.AptNotification;
import com.fc.housebatch.core.job.validator.DealDateParameterValidator;
import com.fc.housebatch.core.repository.AptNotificationRepository;
import com.fc.housebatch.core.repository.LawdRepository;
import com.fc.housebatch.core.service.AptDealService;
import lombok.RequiredArgsConstructor;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AptNotificationJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptNotificationJob(Step aptNotificationStep, DealDateParameterValidator dealDateParameterValidator) {
        return jobBuilderFactory
                .get("aptNotificationJob")
                .incrementer(new RunIdIncrementer())
                .validator(dealDateParameterValidator)
                .start(aptNotificationStep)
                .build();
    }

    /**
     * 
     * @param aptNotificationItemReader : AptNotification 데이터들 중에서 , enabled = true 인 값들 불러오기
     * @param aptNotificationItemProcessor : 가져온 AptNotification 값들 중에서, AptNotification 의 guLawdCd 를 가져오고,
     *                                          AptDeal 중에서 해당 guLawdCd 값과 파라미터로 받은 dealDate 값이 일치하는 데이터들을 AtpDto 들로 가져오고,
     *                                              해당 데이터들을 AptNotificationDto 로 변환해서 반환하기
     * @param aptNotificationItemWriter
     * @return
     */
    @Bean
    @JobScope
    public Step aptNotificationStep(RepositoryItemReader<AptNotification> aptNotificationItemReader,
                                    ItemProcessor<AptNotification, AptNotificationDto> aptNotificationItemProcessor,
                                    ItemWriter<AptNotificationDto> aptNotificationItemWriter) {
        return stepBuilderFactory
                .get("aptNotificationStep")
                .<AptNotification, AptNotificationDto>chunk(10)
                .reader(aptNotificationItemReader)
                .processor(aptNotificationItemProcessor)
                .writer(aptNotificationItemWriter)
                .build();
    }

    /**
     * AptNotification 데이터들 중에서 , enabled = true 인 값들 불러오기
     */
    @Bean
    @StepScope
    public RepositoryItemReader<AptNotification> aptNotificationItemReader(AptNotificationRepository aptNotificationRepository) {
        return new RepositoryItemReaderBuilder<AptNotification>()
                .name("aptNotificationItemReader")
                .repository(aptNotificationRepository)
                .methodName("findByEnabledIsTrue")
                .pageSize(10)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("aptNotificationId", Sort.Direction.DESC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AptNotification, AptNotificationDto> aptNotificationItemProcessor(
            @Value("#{jobParameters['dealDate']}") String dealDate,
            LawdRepository lawdRepository,
            AptDealService aptDealService
    ) {
        return aptNotification -> {
            /**
             * AptNotification 의 guLawdCd 를 가져오고, 
             * AptDeal 중에서 해당 guLawdCd 값과 파라미터로 받은 dealDate 값이 일치하는 데이터들을 AtpDto 들로 가져오기
             */
            List<AptDto> aptDtos = aptDealService.guLawdCdAndDealDateToAtpDto(aptNotification.getGuLawdCd(), LocalDate.parse(dealDate));
            if (aptDtos.isEmpty()) {
                return null;
            }

            String guName = lawdRepository.findByLawdCd(aptNotification.getGuLawdCd() + "00000")
                    .orElseThrow().getLawdDong();

            /**
             * 가져온 데이터들로 AptNotificationDto 만들어서 반환하기
             */
            return AptNotificationDto.builder() // @Builder
                    .email(aptNotification.getEmail())
                    .guName(guName)
                    .count(aptDtos.size())
                    .aptDeals(aptDtos)
                    .build();
        };
    }

    @Bean
    @StepScope
    public ItemWriter<AptNotificationDto> aptNotificationItemWriter() {
        return items -> {
            items.forEach(item -> System.out.println(item.toMessage()));
        };
    }


}

