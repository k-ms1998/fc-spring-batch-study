package com.points.PointsManagementProject;

import com.points.PointsManagementProject.repository.MessageRepository;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointReservationRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BatchTestSupport {

    /**
     * @EnableBatchProcessing 를 추가하면 JobLauncher 과 JobRepository 가 자동으로 빈 등록이 됨
     */
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointReservationRepository pointReservationRepository;

    @Autowired
    private PointWalletRepository pointWalletRepository;

    protected JobExecution launchJob(Job job, JobParameters jobParameters) throws Exception{
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(job);
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);

        return jobLauncherTestUtils.launchJob(jobParameters);
    }

    /**
     * Job 테스트는 자동으로 Rollback 되지 않기 때문에 Job 이 끝날때마다 수동으로 DB에 저장된 값들을 삭제
     */
    @AfterEach
    protected void deleteAll() {
        messageRepository.deleteAll();
        pointRepository.deleteAll();
        pointReservationRepository.deleteAll();
        pointWalletRepository.deleteAll();
    }
}
