package com.points.PointsManagementProject.job.expire;

import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.repository.PointRepository;
import com.points.PointsManagementProject.repository.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ExpirePointStepConfig {

    /**
     *
     * @param stepBuilderFactory
     * @param platformTransactionManager
     * @param expirePointItemReader : ItemReader
     * @param expirePointProcessor : ItemProcessor
     * @param expirePointWriter : ItemWriter
     * @return
     */
    @Bean
    @JobScope // Job 에서 Step 을 실행할 떄 Lazy 하게 생성
    public Step expirePointStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager platformTransactionManager,
                                JpaPagingItemReader<Point> expirePointItemReader, ItemProcessor<Point, Point> expirePointProcessor,
                                ItemWriter<Point> expirePointWriter) {

        return stepBuilderFactory
                .get("expirePointStep")
                .allowStartIfComplete(true) // 같은 조건일 경우 Step 의 중복 실행 가능
                .transactionManager(platformTransactionManager)
                .<Point, Point>chunk(1000) // Chunk Size == 1000
                .reader(expirePointItemReader)
                .processor(expirePointProcessor)
                .writer(expirePointWriter)
                .build();
    }

    /**
     * ItemReader 구현
     * @param entityManagerFactory
     * @param today : @Value("#{T(java.time.LocalDate).parse(jobParameters[today]} 로 String "YYYY-MM--DD" 형식으로 받은 날짜를 LocalDate 를 변환
     * @return
     */
    @Bean
    @StepScope // Step 이 아래의 ItemReader 를 Lazy 하게 생성
    public JpaPagingItemReader<Point> expirePointItemReader(EntityManagerFactory entityManagerFactory,
                                                            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today) {

        return new JpaPagingItemReaderBuilder<Point>()
                .name("expirePointItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Point p where p.expiredDate < :today and isUsed = false and isExpired = false") // JPQL 로 쿼리 작성 => 나중에 QueryDsl 로 변환 할 것
                .parameterValues(Map.of("today", today))    // JPQL 에서 :today 에 toady 값 넣어줌
                .pageSize(1000) // 한 개의 페이지의 사이즈
                .build();
    }

    /**
     * ItemProcessor 구현
     * @return
     */
    @Bean
    @StepScope
    public ItemProcessor<Point, Point> expirePointProcessor() {
        return point -> {   // Point 를 받아와서, 수정하고, 수정한 Point 반환
            point.expire(); // Point 를 만료 상태로 바꾸기

            PointWallet pointWallet = point.getPointWallet();
            Long currentAmount = pointWallet.getAmount();
            Long updatedAmount = currentAmount - point.getAmount();
            pointWallet.updateAmount(updatedAmount);    // PointWallet 에서 Point 의 금액 만큼을 뺀 금액으로 업데이트

            return point;
        };
    }

    /**
     * ItemWriter 구현
     * @param pointRepository
     * @param pointWalletRepository
     * @return
     */
    @Bean
    @StepScope
    public ItemWriter<Point> expirePointItemWriter(PointRepository pointRepository, PointWalletRepository pointWalletRepository) {
        return points -> {
            for (Point point : points) {
                if (point.isExpired()) {
                    pointRepository.save(point);    // Processor 에서 수정한 Point & PointWallet 을 저장
                    pointWalletRepository.save(point.getPointWallet());
                }
            }
        };
    }
}
