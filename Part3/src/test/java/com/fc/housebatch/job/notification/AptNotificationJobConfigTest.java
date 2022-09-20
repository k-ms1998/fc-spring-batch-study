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
        String email1 = "email1@email.com";
        String email2 = "email2@email.com";
        String guLawdCd = "11110";
        LocalDate dealDate = LocalDate.now().minusDays(1);

        givenAptNotification(new String[]{email1, email2}, guLawdCd);
        givenLawdCd(guLawdCd);
        givenAptDeal(guLawdCd, dealDate);

        // When
        JobExecution execution = jobLauncherTestUtils.launchJob(
                new JobParameters(Maps.newHashMap("dealDate", new JobParameter(dealDate.toString()))));

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Mockito.verify(fakeSendService, Mockito.times(1)).send(Mockito.eq(email1), Mockito.anyString());
        Mockito.verify(fakeSendService, Mockito.never()).send(Mockito.eq(email2), Mockito.anyString());

    }

    private void givenAptNotification(String[] emails, String guLawdCd) {
        aptNotificationRepository.save(createAptNotification(emails[0], guLawdCd, true));
        aptNotificationRepository.save(createAptNotification(emails[1], guLawdCd, false));
    }

    private AptNotification createAptNotification(String email, String guLawdCd, boolean enabled) {
        return new AptNotification(email, guLawdCd, enabled,
                        LocalDateTime.now(), LocalDateTime.now());

    }

    private void givenLawdCd(String guLawdCd) {
        String lawdCd = guLawdCd + "00000";
        Mockito.when(lawdRepository.findByLawdCd(lawdCd))
                .thenReturn(Optional.of(new Lawd(lawdCd, "경기도 성남시 분당구", true)));
    }

    private void givenAptDeal(String guLawdCd, LocalDate dealDate) {
        Mockito.when(aptDealService.guLawdCdAndDealDateToAtpDto(guLawdCd, dealDate))
                .thenReturn(Arrays.asList(
                        new AptDto("Apt A", 200000000L),
                        new AptDto("Apt B", 100000000L)
                ));

    }

}
