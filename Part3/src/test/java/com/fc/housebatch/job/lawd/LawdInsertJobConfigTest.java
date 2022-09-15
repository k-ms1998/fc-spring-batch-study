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
         * lawdService �� upsertLawd �� �� ��� ȣ��Ǵ��� Ȯ��; 
         * 
         * TEST_LAWD_CODE �� �����Ͱ� 3�� �����Ƿ�, lawdService.upsertLawd �� �� 3�� ȣ��Ǿ�� ��
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
         * 1. launchJob �� ���� ������ JobParametersInvalidException �� �߻��ϸ� ��
         * 2. LawdService �� upsert() �� ȣ���� �ѹ��� ���� �ʾƾ� ��
         */
        Assertions.assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob(parameters));
        Mockito.verify(lawdService, Mockito.never()).upsertLawd(any());

    }
}