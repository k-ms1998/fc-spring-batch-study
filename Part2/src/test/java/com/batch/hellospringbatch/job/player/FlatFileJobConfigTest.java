package com.batch.hellospringbatch.job.player;

import com.batch.hellospringbatch.BatchTestConfig;
import com.batch.hellospringbatch.core.service.PlayerSalaryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {FlatFileJobConfig.class, BatchTestConfig.class, PlayerSalaryService.class})
/** !! FlatFileJobConfig.class => FlatFileJobConfig 의 Job 를 가져와서 JobLauncherTestUtils 의 setJob() 에 자동으로 주입해줌 !!
 * => 그러므로, jobLauncherTestUtils.launchJob() 실행 시, 'flatFileJob' 실행됨
 */ 
class FlatFileJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void given_when_then() throws Exception {
        // Given

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        /**
         * 두 개 파일의 값이 같은 지 확인
         * 이때, 두 파일의 값을 라인 별로 같은지 확인하기 때문에, 데이터의 순서도 같아야됨
         */
        AssertFile.assertFileEquals(new FileSystemResource("player-salary-list.txt"),
                new FileSystemResource("succeed-player-salary-list.txt"));

    }

}