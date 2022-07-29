package com.points.PointsManagementProject.job.message;

import com.points.PointsManagementProject.domain.ExpiredPointSummary;
import com.points.PointsManagementProject.domain.Message;
import com.points.PointsManagementProject.job.listener.InputExpiredPointAlarmCriteriaDateStepListener;
import com.points.PointsManagementProject.job.listener.InputExpiringSoonPointAlarmCriteriaDateStepListener;
import com.points.PointsManagementProject.repository.PointRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class MessageExpiringSoonPointStepConfig {

    @Bean
    @JobScope
    public Step messageExpiringSoonPointStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager platformTransactionManager,
                                             InputExpiringSoonPointAlarmCriteriaDateStepListener listener,
                                             RepositoryItemReader<ExpiredPointSummary> messageExpiringSoonPointItemReader,
                                             ItemProcessor<ExpiredPointSummary, Message> messageExpiringSoonPointItemProcessor,
                                             ItemWriter<Message> messageExpiringSoonPointItemWriter) {

        return stepBuilderFactory
                .get("messageExpiringSoonPointStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .listener(listener)
                .<ExpiredPointSummary, Message>chunk(1000)
                .reader(messageExpiringSoonPointItemReader)
                .processor(messageExpiringSoonPointItemProcessor)
                .writer(messageExpiringSoonPointItemWriter)
                .build();
    }

    /**
     * alarmCriteriaDate : InputExpiredPointAlarmCriteriaDateStepListener 에서 ("alarmCriteriaDate", 어제 날짜) 를 넣어줬던것을 가져오기
     */
    @Bean
    @StepScope
    public RepositoryItemReader<ExpiredPointSummary> messageExpiringSoonPointItemReader(
            PointRepository pointRepository,
            @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                    LocalDate alarmCriteriaDate) {
        
        return new RepositoryItemReaderBuilder<ExpiredPointSummary>()
                .name("messageExpiringSoonPointItemReader")
                .repository(pointRepository)
                .methodName("sumBeforeCriteriaDate") // pointRepository 에서 sumBeforeCriteriaDate 메서드 호출
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
    public ItemProcessor<ExpiredPointSummary, Message> messageExpiringSoonPointItemProcessor(
            @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                    LocalDate alarmCriteriaDate
    ) {
        return expiredPointSummary -> {
            String userId = expiredPointSummary.getUserId();
            Long amount = expiredPointSummary.getAmount();

            Message expiredPointMessage = Message.expiringSoonPointMessageInstance(userId, amount, alarmCriteriaDate);
            return expiredPointMessage;
        };
        // == 'return expiredPointSummary -> Message.expiringSoonPointMessageInstance(userId, amount, today);'
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
    public JpaItemWriter<Message> messageExpiringSoonPointItemWriter(EntityManagerFactory entityManagerFactory) {
        /**
         * Hibernate: insert into message (id, content, title, user_id) values (default, ?, ?, ?)
         */
        JpaItemWriter<Message> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);

        return jpaItemWriter;
    }
}
