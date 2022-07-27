package com.points.PointsManagementProject.job.expire;

import com.points.PointsManagementProject.BatchTestSupport;
import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class ExpirePointJobConfigTest extends BatchTestSupport {
    /**
     * ExpirePointJobConfig 에서 expirePointJob 가져옴 => ** 이때, ExpirePointJobConfig 에서 expirePointJob 를 빈 등록해서 자동으로 가져옴 **
     */
    @Autowired
    Job expirePointJob;

    @Autowired
    PointWalletRepository pointWalletRepository;

    @Autowired
    PointRepository pointRepository;

    @Test
    void givenPointWalletAndPoint_whenExecuteExpirePointJob_then() throws Exception{
        LocalDate earnedDate = LocalDate.of(2022, 1, 1);
        LocalDate expiredDate = LocalDate.of(2022, 1, 3);

        // given
        PointWallet pointWallet = pointWalletRepository.save(new PointWallet(6000L, "userA"));
        Point pointA = pointRepository.save(new Point(1000L, earnedDate, expiredDate, pointWallet));
        Point pointB = pointRepository.save(new Point(1000L, earnedDate, expiredDate, pointWallet));
        Point pointC = pointRepository.save(new Point(1000L, earnedDate, expiredDate, pointWallet));

        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2022-01-04")    // "today" : "2022-01-04" 파라미터 추가 -> 현재 시점 -> 만료 날짜를 이미 지나간 날짜
                .toJobParameters();
        JobExecution result = launchJob(expirePointJob, jobParameters);// BatchTestSupport.launchJob(job, jobParameters)

        /**
         * Job에서 할 일:
         * 1. pointA, pointB, pointC 만료 시키기
         * 2. pointWallet 의 적립 금액 업데이트 시키기
         */
        // then
        then(result.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        /**
         *  Job 를 거치면 pointA, pointB, pointC 는 모두 만료되어야 한다
         */
        List<Point> points = pointRepository.findAll();
        then(points.stream().filter(p -> {
            if (p.isExpired()) {
                return true;
            }
            return false;
        })).hasSize(3); // 모든 Point 들을 가져와서, 만료된 갯수 확인

        /**
         *  PointWallet 에 적립된 금액 확인
         *  처음에는 pointA, pointB, pointC 가 포함되어 있어서 총 6000 포인트 존재
         *  Job 를 거치면 pointA, pointB, pointC 가 제외되기 떄문에 3000 포인트 감소 => 남은 포인트 == 3000
         */
        PointWallet changedPointWallet = pointWalletRepository.findById(pointWallet.getId()).orElseGet(null);
        then(changedPointWallet).isNotNull();
        then(changedPointWallet.getAmount()).isEqualTo(3000L);
    }
}