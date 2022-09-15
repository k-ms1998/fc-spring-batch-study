package com.fc.housebatch.job.apt;

import com.fc.housebatch.BatchTestConfig;
import com.fc.housebatch.adapter.ApartmentApiResource;
import com.fc.housebatch.core.job.apt.AptDealInsertJobConfig;
import com.fc.housebatch.core.repository.LawdRepository;
import com.fc.housebatch.core.service.AptDealService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptDealInsertJobConfig.class, BatchTestConfig.class})
public class AptDealInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private AptDealService aptDealService;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private ApartmentApiResource apartmentApiResource;

    @Test
    void givenMockData_whenLaunchingJob_thenSuccess() throws Exception{
        // Given
        /**
         * lawdRepository.findAllDistinctGuLawdCd() �� ȣ��Ǹ� "11110", "41135"�� ��ȯ�ϵ��� ��
         */
        Mockito.when(lawdRepository.findAllDistinctGuLawdCd()).thenReturn(Arrays.asList("11110", "41135"));

        /**
         * apartmentApiResource.getResource() �� ȣ��Ǹ� test-api-response.xml ��ȯ
         */
        Mockito.when(apartmentApiResource.getResource(Mockito.anyString(), Mockito.any())).thenReturn(new ClassPathResource("test-api-response.xml"));

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob(new JobParameters(
                Maps.newHashMap("yearMonth", new JobParameter("2021-07"))
        ));
        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        /**
         * lawdRepository.findAllDistinctGuLawdCd() �� ȣ��Ǹ� "11110", "41135"�� ��ȯ
         * �� guLawdCd ��("11110", "41135") ���� "test-api-response.xml" �� �а� upsert ȣ��
         * -> "test-api-response.xml"�� �����Ͱ� 3�� ��������Ƿ�, �� guLawdCd ������ 3���� upsert ȣ��
         *  -> �׷��Ƿ�, �� 6�� ȣ���
         */
        Mockito.verify(aptDealService, Mockito.times(6)).upsert(Mockito.any());

    }

    @Test
    void givenInvalidYearMonth_whenLaunchingJob_thenFail() throws Exception{
        // Given
        /**
         * lawdRepository.findAllDistinctGuLawdCd() �� ȣ��Ǹ� "11110"�� ��ȯ�ϵ��� ��
         */
        Mockito.when(lawdRepository.findAllDistinctGuLawdCd()).thenReturn(Arrays.asList("11110"));

        /**
         * apartmentApiResource.getResource() �� ȣ��Ǹ� test-api-response.xml ��ȯ
         */
        Mockito.when(apartmentApiResource.getResource(Mockito.anyString(), Mockito.any())).thenReturn(new ClassPathResource("test-api-response.xml"));

        // When && Then
        /**
         * yearMonth �Ķ���� ���� ���� ���� ���·� launchJob ���� -> JobParametersInvalidException �߻�
         */
        Assertions.assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob(new JobParameters()));
        Mockito.verify(aptDealService, Mockito.never()).upsert(Mockito.any());

    }

}
