package com.batch.hellospringbatch.job;

import com.batch.hellospringbatch.BatchTestConfig;
import com.batch.hellospringbatch.core.domain.PlainText;
import com.batch.hellospringbatch.core.repository.PlainTextRepository;
import com.batch.hellospringbatch.core.repository.ResultTextRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") // application.yml 에서의 'test' profiles 로 설정해줌
@ContextConfiguration(classes = {PlainTextJobConfig.class, BatchTestConfig.class})
        // !! PlainTextJobConfig.class => PlainTextJobConfig 의 Job 를 가져와서 JobLauncherTestUtils 의 setJob() 에 자동으로 주입해줌 !!
class PlainTextJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PlainTextRepository plainTextRepository;

    @Autowired
    private ResultTextRepository resultTextRepository;

    @AfterEach // 각 테스트가 끝날때마다 실행
    public void tearDown() {
        plainTextRepository.deleteAll();
        resultTextRepository.deleteAll();
    }

    @Test
    void givenNoPlainText_whenExecutingPlainTextJob_thenSuccess() throws Exception {
        // Given
        // nothing

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        /**
         * 1. Completed
         * 2. ResultText 에 총 0개의 튜플 저장됨
         */
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(resultTextRepository.count(), 0);

    }

    @Test
    void givenPlainText_whenExecutingPlainTextJob_thenSuccess() throws Exception {
        // Given
        givenPlainTexts(12);

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        /**
         * 1. Completed
         * 2. ResultText 에 총 12개의 튜플 저장됨
         */
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(resultTextRepository.count(), 12);

    }

    private void givenPlainTexts(int count) {
        IntStream.range(0, count)
                .forEach(num -> plainTextRepository.save(new PlainText("text" + num)));
    }

}