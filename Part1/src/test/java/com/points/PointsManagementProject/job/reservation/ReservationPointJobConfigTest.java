package com.points.PointsManagementProject.job.reservation;

import com.points.PointsManagementProject.BatchTestSupport;
import com.points.PointsManagementProject.domain.PointReservation;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointReservationRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationPointJobConfigTest extends BatchTestSupport {

    @Autowired
    Job reservationPointJob;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointWalletRepository pointWalletRepository;

    @Autowired
    private PointReservationRepository pointReservationRepository;

    /**
     * 각 테스트가 종료 될때마다 실행
     * -> 각 테스트 종료마다 각 테이블에 저장된 값들 삭제 (주의: FK Constraints 에 의해 삭제 순서 중요)
     */
    @AfterEach
    public void tearDown() {
        pointRepository.deleteAll();
        pointReservationRepository.deleteAll();
        pointWalletRepository.deleteAll();
    }

    @Test
    void given_whenExecutingReservationPointJob_then() throws Exception{
        LocalDate earnedDate = LocalDate.of(2022, 1, 5);

        // Given
        PointWallet pointWallet = pointWalletRepository.save(new PointWallet(3000L, "userA"));
        PointReservation pointReservation = pointReservationRepository.save(new PointReservation(1000L, earnedDate, 10, pointWallet));
        // 1000 point 가 earnedDate 부터 10일 동안 유효
        
        // When
         /**
         * 1. Point Reservation 완료 처리 -> isExecuted = true
         * 2. Point 적립 완료
         * 3. PointWallet 의 amount 증가
         */
        JobParameters jobParameters= new JobParametersBuilder()
                .addString("today", "2022-01-05")
                .toJobParameters();
        JobExecution result = launchJob(reservationPointJob, jobParameters);

        // Then
        then(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        /**
         * 1. Point Reservation 완료 처리 -> isExecuted = true
         */
        then(pointReservationRepository.findById(pointReservation.getId()).get().isExecuted()).isTrue();

        /**
         * 2. Point 적립 완료:
         * Point 테이블에 저장된 값이 없었는데, 하나 추가 됐으므로 모든 Point 들을 가져왔을때 갯수가 1인지 확인
         */
        then(pointRepository.findAll()).hasSize(1);
        then(pointRepository.findAll().get(0).getExpiredDate()).isEqualTo(LocalDate.of(2022, 1, 15));

        /**
         * 3. PointWallet 의 amount 증가:
         * PointWallet 의 amount 가 3000 이였는데 1000 추가 됐으므로, 4000인지 확인
         */
        then(pointWalletRepository.findById(pointWallet.getId()).get().getAmount()).isEqualTo(4000L);
    }
}