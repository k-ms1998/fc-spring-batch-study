package com.fc.housebatch.job.lawd;

import com.fc.housebatch.BatchTestConfig;
import com.fc.housebatch.core.entity.Lawd;
import com.fc.housebatch.core.job.lawd.LawdInsertJobConfig;
import com.fc.housebatch.core.repository.LawdRepository;
import com.fc.housebatch.core.service.LawdService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {LawdInsertJobConfig.class, BatchTestConfig.class})
class LawdInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private LawdService lawdService;

    static final String FILE_PATH = "filePath";

    @Test
    void givenValidPath_whenLaunchingJob_thenSuccess() throws Exception {
        // Given
        String filePathName = "TEST_LAWD_CODE.txt";

        // When
        JobParameters parameters = new JobParameters(Maps.newHashMap(FILE_PATH, new JobParameter(filePathName)));
        JobExecution execution = jobLauncherTestUtils.launchJob(parameters);

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        /**
         * lawdService 의 upsertLawd 가 총 몇번 호출되는지 확인; 
         * 
         * TEST_LAWD_CODE 에 데이터가 3개 있으므로, lawdService.upsertLawd 가 총 3번 호출되어야 함
         * 
         */
        Mockito.verify(lawdService, Mockito.times(3)).upsertLawd(any());

    }

    @Test
    void givenInvalidPath_whenLaunchingJob_thenFailFileNotFound() throws Exception {
        // Given
        String filePathName = "INVALID_PATH.txt";

        // When
        JobParameters parameters = new JobParameters(Maps.newHashMap(FILE_PATH, new JobParameter(filePathName)));

        // Then
        /**
         * 1. launchJob 을 실행 했을때 JobParametersInvalidException 이 발생하면 됨
         * 2. LawdService 의 upsert() 가 호출이 한번도 되지 않아야 함
         */
        Assertions.assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob(parameters));
        Mockito.verify(lawdService, Mockito.never()).upsertLawd(any());

    }
}