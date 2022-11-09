package com.fc.project.batch.job;

import com.fc.project.batch.job.dto.SendMailBatchRequest;
import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SendEmailNotificationConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 4;

    @Bean
    public Job sendEmailNotificationJob(Step sendScheduleNotificationStep, Step sendEngagementNotificationStep) {
        return jobBuilderFactory.get("sendEmailNotificationJob")
                .start(sendScheduleNotificationStep)
                .next(sendEngagementNotificationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step sendScheduleNotificationStep(JdbcCursorItemReader<SendMailBatchRequest> scheduleItemReader,
                                             ItemWriter<SendMailBatchRequest> sendNotificationItemWriter) {
        return stepBuilderFactory.get("sendScheduleNotificationStep")
                .<SendMailBatchRequest, SendMailBatchRequest>chunk(CHUNK_SIZE)
                .reader(scheduleItemReader)
                .processor(new ItemProcessor<SendMailBatchRequest, SendMailBatchRequest>() {
                    @Override
                    public SendMailBatchRequest process(SendMailBatchRequest item) throws Exception {
                        System.out.println("----- [PROCESSOR SCHEDULE] -----");
                        return item;
                    }
                })
                .writer(sendNotificationItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @JobScope
    public Step sendEngagementNotificationStep(JdbcCursorItemReader<SendMailBatchRequest> engagementItemReader,
                                               ItemWriter<SendMailBatchRequest> sendNotificationItemWriter) {
        return stepBuilderFactory.get("sendEngagementNotificationStep")
                .<SendMailBatchRequest, SendMailBatchRequest>chunk(CHUNK_SIZE)
                .reader(engagementItemReader)
                .processor(new ItemProcessor<SendMailBatchRequest, SendMailBatchRequest>() {
                    @Override
                    public SendMailBatchRequest process(SendMailBatchRequest item) throws Exception {
                        System.out.println("----- [PROCESSOR ENGAGEMENT] -----");
                        return item;
                    }
                })
                .writer(sendNotificationItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<SendMailBatchRequest> scheduleItemReader(){
        return new JdbcCursorItemReaderBuilder<SendMailBatchRequest>()
                .name("scheduleItemReader")
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(SendMailBatchRequest.class))
                .sql("SELECT * FROM schedules s INNER JOIN users u ON s.user_id = u.id " +
                        "WHERE s.start_at >= now() - interval 10 minute " +
                        "AND s.start_at < now() + interval 11 minute")
                .build();
    }


    @Bean
    @StepScope
    public JdbcCursorItemReader<SendMailBatchRequest> engagementItemReader(){
        return new JdbcCursorItemReaderBuilder<SendMailBatchRequest>()
                .name("engagementItemReader")
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(SendMailBatchRequest.class))
                .sql("SELECT * FROM engagement e " +
                        "INNER JOIN schedules s ON e.schedule_id = s.id " +
                        "INNER JOIN users u ON s.user_id = u.id " +
                        "WHERE s.start_at >= now() - interval 10 minute " +
                        "AND s.start_at < now() + interval 11 minute " +
                        "AND e.request_status = 'ACCEPTED'")
                .build();
    }

    @Bean
    public ItemWriter<SendMailBatchRequest> sendNotificationItemWriter() {
        return s ->
            new RestTemplate()
                    .postForObject("http://localhost:8080/batch/mail", s, Object.class);
    }
}
