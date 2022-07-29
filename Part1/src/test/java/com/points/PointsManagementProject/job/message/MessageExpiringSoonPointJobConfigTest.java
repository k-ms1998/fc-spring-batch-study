package com.points.PointsManagementProject.job.message;

import com.points.PointsManagementProject.BatchTestSupport;
import com.points.PointsManagementProject.domain.Message;
import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.repository.MessageRepository;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class MessageExpiringSoonPointJobConfigTest extends BatchTestSupport {

    @Autowired
    Job messageExpiringSoonPointJob;

    @Autowired
    PointWalletRepository pointWalletRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    PointRepository pointRepository;

    @Test
    void given_whenExecutingMessageExpiredPointJob_then() throws Exception{
        LocalDate earnDate = LocalDate.of(2022, 1, 1);
        LocalDate expireDate = LocalDate.of(2022, 7, 5);
        LocalDate notExpireDate = LocalDate.of(2022, 7, 12);

        // Given
        PointWallet pointWallet1 = pointWalletRepository.save(new PointWallet(3000L, "user1"));
        PointWallet pointWallet2 = pointWalletRepository.save(new PointWallet(2000L, "user2"));

        pointRepository.save(new Point(1000L, earnDate, notExpireDate, pointWallet2));
        pointRepository.save(new Point(1000L, earnDate, notExpireDate, pointWallet2));

        pointRepository.save(new Point(1000L, earnDate, expireDate, pointWallet1, true));
        pointRepository.save(new Point(1000L, earnDate, expireDate, pointWallet1, true));
        pointRepository.save(new Point(1000L, earnDate, expireDate, pointWallet1, true));

        pointRepository.save(new Point(1000L , earnDate, notExpireDate, pointWallet1));
        pointRepository.save(new Point(1000L , earnDate, notExpireDate, pointWallet1));
        pointRepository.save(new Point(1000L , earnDate, notExpireDate, pointWallet1));

        // When
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2022-07-06")
                .toJobParameters();
        JobExecution result = launchJob(messageExpiringSoonPointJob, jobParameters);

        // Then
        /**
         * 1. user1과 user2 에게 각각 포인트 만료 메세지 보냄 -> message 의 갯수 == 2
         * 2. user1에게 보낸 메세지 일치하는지 확인 
         * 3. user2에게 보낸 메세지 일치하는지 확인 
         */
        then(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        List<Message> messages = messageRepository.findAll();
        then(messages).hasSize(2);

        Message message1 = messages.stream().filter(item -> item.getUserId().equals("user1")).findFirst().orElseGet(null);
        then(message1).isNotNull();
        then(message1.getTitle()).isEqualTo("3000 포인트 만료 예정");
        then(message1.getContent()).isEqualTo("2022-07-13 까지 3000 포인트가 만료 예정입니다.");
        
        Message message2 = messages.stream().filter(item -> item.getUserId().equals("user2")).findFirst().orElseGet(null);
        then(message2).isNotNull();
        then(message2.getTitle()).isEqualTo("2000 포인트 만료 예정");
        then(message2.getContent()).isEqualTo("2022-07-13 까지 2000 포인트가 만료 예정입니다.");
    }

}