package com.fc.housebatch.job.notification;

import com.fc.housebatch.BatchTestConfig;
import com.fc.housebatch.adapter.FakeSendService;
import com.fc.housebatch.core.dto.AptDto;
import com.fc.housebatch.core.entity.AptNotification;
import com.fc.housebatch.core.entity.Lawd;
import com.fc.housebatch.core.job.notification.AptNotificationJobConfig;
import com.fc.housebatch.core.job.validator.DealDateParameterValidator;
import com.fc.housebatch.core.repository.AptNotificationRepository;
import com.fc.housebatch.core.repository.LawdRepository;
import com.fc.housebatch.core.service.AptDealService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@SpringBatchTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptNotificationJobConfig.class, BatchTestConfig.class})
public class AptNotificationJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AptNotificationRepository aptNotificationRepository;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private AptDealService aptDealService;

    @MockBean
    private FakeSendService fakeSendService;

    @AfterEach
    public void tearDown() {
        aptNotificationRepository.deleteAll();
    }

    @Test
    void given_when_thenSuccess() throws Exception {
        // Given
        LocalDate dealDate = LocalDate.now().minusDays(1);
        givenAptNotification();
        givenLawdCd();
        givenAptDeal();

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob(
                new JobParameters(Maps.newHashMap("dealDate", new JobParameter(dealDate.toString()))));

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Mockito.verify(fakeSendService, Mockito.times(1)).send(Mockito.anyString(), Mockito.anyString());

    }

    private void givenAptNotification() {
        AptNotification aptNotification =
                new AptNotification("email1@email.com", "11110", true,
                        LocalDateTime.now(), LocalDateTime.now());

        aptNotificationRepository.save(aptNotification);
    }

    private void givenLawdCd() {
        Mockito.when(lawdRepository.findByLawdCd("1111000000"))
                .thenReturn(Optional.of(new Lawd("1111000000", "경기도 성남시 분당구", true)));
    }

    private void givenAptDeal() {
        Mockito.when(aptDealService.guLawdCdAndDealDateToAtpDto("11110", LocalDate.now().minusDays(1)))
                .thenReturn(Arrays.asList(
                        new AptDto("Apt A", 200000000L),
                        new AptDto("Apt B", 100000000L)
                ));

    }

}
