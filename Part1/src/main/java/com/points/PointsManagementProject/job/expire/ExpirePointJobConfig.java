package com.points.PointsManagementProject.job.expire;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirePointJobConfig {

    /**
     *
     * @param jobBuilderFactory
     * @param expirePointStep : ExpirePointStepConfig 의 expirePointStep 가져옴 => ** 이때, expirePointStep 은 빈 등록을 했기 때문에 자동으로 주입되 **
     * @return
     */
    @Bean
    public Job expirePointJob(JobBuilderFactory jobBuilderFactory, Step expirePointStep) {

        return jobBuilderFactory.get("expirePointJob")
                .start(expirePointStep)
                .build();
    }
}
