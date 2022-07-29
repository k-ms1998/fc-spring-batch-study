package com.points.PointsManagementProject.job.message;

import com.points.PointsManagementProject.job.validator.JobTodayParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageExpiringSoonPointJobConfig {

    @Bean
    public Job messageExpiringSoonPointJob(JobBuilderFactory jobBuilderFactory,
                                      JobTodayParameterValidator validator,
                                      Step messageExpiringSoonPointStep) {
        return jobBuilderFactory
                .get("messageExpiringSoonPointJob")
                .start(messageExpiringSoonPointStep)
                .validator(validator)
                .incrementer(new RunIdIncrementer())
                .build();
    }

}
