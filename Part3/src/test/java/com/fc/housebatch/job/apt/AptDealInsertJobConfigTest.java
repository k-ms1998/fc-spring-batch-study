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
         * lawdRepository.findAllDistinctGuLawdCd() 가 호출되면 "11110", "41135"를 반환하도록 함
         */
        Mockito.when(lawdRepository.findAllDistinctGuLawdCd()).thenReturn(Arrays.asList("11110", "41135"));

        /**
         * apartmentApiResource.getResource() 가 호출되면 test-api-response.xml 반환
         */
        Mockito.when(apartmentApiResource.getResource(Mockito.anyString(), Mockito.any())).thenReturn(new ClassPathResource("test-api-response.xml"));

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob(new JobParameters(
                Maps.newHashMap("yearMonth", new JobParameter("2021-07"))
        ));
        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        /**
         * lawdRepository.findAllDistinctGuLawdCd() 가 호출되면 "11110", "41135"를 반환
         * 각 guLawdCd 값("11110", "41135") 마다 "test-api-response.xml" 를 읽고 upsert 호출
         * -> "test-api-response.xml"에 데이터가 3개 들어있으므로, 각 guLawdCd 값마다 3번씩 upsert 호출
         *  -> 그러므로, 총 6번 호출됨
         */
        Mockito.verify(aptDealService, Mockito.times(6)).upsert(Mockito.any());

    }

    @Test
    void givenInvalidYearMonth_whenLaunchingJob_thenFail() throws Exception{
        // Given
        /**
         * lawdRepository.findAllDistinctGuLawdCd() 가 호출되면 "11110"를 반환하도록 함
         */
        Mockito.when(lawdRepository.findAllDistinctGuLawdCd()).thenReturn(Arrays.asList("11110"));

        /**
         * apartmentApiResource.getResource() 가 호출되면 test-api-response.xml 반환
         */
        Mockito.when(apartmentApiResource.getResource(Mockito.anyString(), Mockito.any())).thenReturn(new ClassPathResource("test-api-response.xml"));

        // When && Then
        /**
         * yearMonth 파라미터 값을 주지 않은 상태로 launchJob 실행 -> JobParametersInvalidException 발생
         */
        Assertions.assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob(new JobParameters()));
        Mockito.verify(aptDealService, Mockito.never()).upsert(Mockito.any());

    }

}
