package com.points.PointsManagementProject.job.message;

import com.points.PointsManagementProject.domain.ExpiredPointSummary;
import com.points.PointsManagementProject.domain.Message;
import com.points.PointsManagementProject.job.listener.InputExpiredPointAlarmCriteriaDateStepListener;
import com.points.PointsManagementProject.job.validator.JobTodayParameterValidator;
import com.points.PointsManagementProject.repository.MessageRepository;
import com.points.PointsManagementProject.repository.PointRepository;
import lombok.Getter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class MessageExpiredPointStepConfig {

    @Bean
    @JobScope
    public Step messageExpiredPointStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager platformTransactionManager,
                                        InputExpiredPointAlarmCriteriaDateStepListener listener,
                                        RepositoryItemReader<ExpiredPointSummary> messageExpiredPointItemReader,
                                        ItemProcessor<ExpiredPointSummary, Message> messageExpiredPointItemProcessor,
                                        ItemWriter<Message> messageExpiredPointItemWriter) {

        return stepBuilderFactory
                .get("messageExpiredPointStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .listener(listener)
                .<ExpiredPointSummary, Message>chunk(1000)
                .reader(messageExpiredPointItemReader)
                .processor(messageExpiredPointItemProcessor)
                .writer(messageExpiredPointItemWriter)
                .build();
    }

    /**
     * alarmCriteriaDate : InputExpiredPointAlarmCriteriaDateStepListener 에서 ("alarmCriteriaDate", 어제 날짜) 를 넣어줬던것을 가져오기
     */
    @Bean
    @StepScope
    public RepositoryItemReader<ExpiredPointSummary> messageExpiredPointItemReader(
            PointRepository pointRepository,
            @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                    LocalDate alarmCriteriaDate) {
        
        return new RepositoryItemReaderBuilder<ExpiredPointSummary>()
                .name("messageExpiredPointItemReader")
                .repository(pointRepository)
                .methodName("sumByExpiredDate") // pointRepository 에서 sumByExpiredDate 메서드 호출
                .arguments(alarmCriteriaDate) // pointRepository.sumByExpiredDate 에 파라미터 주입
                .pageSize(1000)
                .sorts(Map.of("pointWallet", Sort.Direction.ASC))
                .build();
    }

    /**
     * 목표: ExpiredPointSummary 로 Message 생성하기
     * 
     * 인자로 오늘 날짜를 Job Parameter 에서 가져옴
     * @return
     */
    @Bean
    @StepScope
    public ItemProcessor<ExpiredPointSummary, Message> messageExpiredPointItemProcessor(
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today
    ) {
        return expiredPointSummary -> {
            String userId = expiredPointSummary.getUserId();
            Long amount = expiredPointSummary.getAmount();

            Message expiredPointMessage = Message.expiredPointMessageInstance(userId, amount, today);
            return expiredPointMessage;
        };
        // == 'return expiredPointSummary -> Message.expiredPointMessageInstance(userId, amount, today);'
    }

    /**
     * 목표: ItemProcessor 에서 ExpiredPointSummary 에서 변환된 Message 들을 저장
     *
     * !! JpaItemWriter !!
     * 1. Writer 에 전달하는 데이터가 Entity 면 JpaItemWriter 를 사용하면 된다
     * 2. ItemProcessor 에서 넘어온 Entity 를 DB에 반영
     *
     * @return
     */
    @Bean
    @StepScope
    public JpaItemWriter<Message> messageExpiredPointItemWriter(EntityManagerFactory entityManagerFactory) {
        /**
         * Hibernate: insert into message (id, content, title, user_id) values (default, ?, ?, ?)
         */
        JpaItemWriter<Message> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return jpaItemWriter;
    }
}
