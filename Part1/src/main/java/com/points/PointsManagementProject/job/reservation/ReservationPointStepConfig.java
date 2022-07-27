package com.points.PointsManagementProject.job.reservation;

import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.PointReservation;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointReservationRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import lombok.Getter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ReservationPointStepConfig {

    /**
     * ItemReader<A>, ItemProcessor<A, B>, ItemWriter<B>
     */
    @Bean
    @JobScope
    public Step reservationPointStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager platformTransactionManager,
                                     JpaPagingItemReader<PointReservation> reservationPointItemReader,
                                     ItemProcessor<PointReservation, WriterDto> reservationPointItemProcessor,
                                     ItemWriter<WriterDto> reservationPointItemWriter) {
        return stepBuilderFactory
                .get("reservationPointStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .<PointReservation, WriterDto>chunk(1000)
                .reader(reservationPointItemReader)
                .processor(reservationPointItemProcessor)
                .writer(reservationPointItemWriter)
                .build();
    }

    /**
     * ItemReader<PointReservation> 구현:
     * DB에서 적립이 예약된 PointReservation 들 모두 가져오기
     *  -> 획득 날짜가 오늘이고, isExecuted == false 이면 가져오기
     * @return
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<PointReservation> reservationPointItemReader(EntityManagerFactory entityManagerFactory,
                                                                            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today) {

        return new JpaPagingItemReaderBuilder<PointReservation>()
                .name("reservationPointItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select pr from PointReservation pr where pr.earnedDate = :today and pr.isExecuted = false")
                .parameterValues(Map.of("today", today))    // 쿼리의 :today 에 값 대입
                .pageSize(1000)
                .build();
    }

    /**
     * ItemProcessor<PointReservation, WriterDto> 구현:
     * 0. Input == PointReservation, Output == Point
     * 1. PointReservation 의 isExecuted 를 true 로 업데이트
     * 2. PointWallet 의 금액 업데이트
     * 3. PointReservation 의 값들로 Point 생성
     * 
     * !! 실제 DB 에 업데이트된 값들을 저장하는 작업은 ItemWriter 에서 진행 !!
     * -> 이때, 저장해야되는 객체들이 Point, PointReservation, PointWallet 인데, ItemProcessor 에서 ItemWriter 로 넘길수 있는 객체는 하나의 종류 밖에 없음
     *      => ** 그러므로, Point, PointReservation, PointWallet 을 담고 있는 DTO 너겨줌 **
     * 
     * @return
     */
    @Bean
    @StepScope
    public ItemProcessor<PointReservation, WriterDto> reservationPointItemProcessor() {
        return pointReservation -> {
            pointReservation.executed(); // isExecuted = true

            PointWallet pointWallet = pointReservation.getPointWallet();
            Long currentAmount = pointWallet.getAmount();
            Long updatedAmount = currentAmount + pointReservation.getAmount();
            pointWallet.updateAmount(updatedAmount);

            Long amount = pointReservation.getAmount();
            LocalDate earnedDate = pointReservation.getEarnedDate();
            LocalDate expiredDate = pointReservation.getExpiryDate();

            Point point = new Point(amount, earnedDate, expiredDate, pointWallet);

            // ** Point, PointReservation, PointWallet 을 담고 있는 DTO 너겨줌 **
            return new WriterDto(point, pointReservation, pointWallet);
        };
    }

    /**
     * ItemWriter<WriterDto> 구현:
     * 1. ItemProcessor 에서 생성된 Point 객체들 저장
     * 2. ItemProcessor 에서 업데이트된 PointReservation 객체들 저장
     * 3. ItemProcessor 에서 업데이트된 PointWallet 객체들 저장
     * @return
     */
    @Bean
    @StepScope
    public ItemWriter<WriterDto> reservationPointItemWriter(PointReservationRepository pointReservationRepository,
                                                            PointRepository pointRepository,
                                                            PointWalletRepository pointWalletRepository) {
        return writerDtos -> {
            for (WriterDto dto : writerDtos) {
                Point point = dto.getPoint();
                PointReservation pointReservation = dto.getPointReservation();
                PointWallet pointWallet = dto.getPointWallet();

                pointRepository.save(point);
                pointReservationRepository.save(pointReservation);
                pointWalletRepository.save(pointWallet);
            }
        };
    }

    @Getter
    private static class WriterDto{
        Point point;
        PointReservation pointReservation;
        PointWallet pointWallet;

        public WriterDto(Point point, PointReservation pointReservation, PointWallet pointWallet) {
            this.point = point;
            this.pointReservation = pointReservation;
            this.pointWallet = pointWallet;
        }
    }
}
