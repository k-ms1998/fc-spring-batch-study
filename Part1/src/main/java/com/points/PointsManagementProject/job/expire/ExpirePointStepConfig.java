package com.points.PointsManagementProject.job.expire;

import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.PointWallet;
import com.points.PointsManagementProject.job.readerCustom.ReverseJpaPagingItemReader;
import com.points.PointsManagementProject.job.readerCustom.ReverseJpaPagingItemReaderBuilder;
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
import org.springframework.data.domain.Sort;
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
                                ReverseJpaPagingItemReader<Point> expirePointItemReader, ItemProcessor<Point, Point> expirePointProcessor,
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
     * ItemReader 구현:
     * DB에 저장된 Point 들 중에서 , 이미 만료 일자가 지났고, 사용되지 않았으며, 현재 만료가 되지 않았다는 상태의 Point 들 가져오기
     *
     * @param today : @Value("#{T(java.time.LocalDate).parse(jobParameters[today]} 로 String "YYYY-MM--DD" 형식으로 받은 날짜를 LocalDate 를 변환
     * @return
     */
    @Bean
    @StepScope // Step 이 아래의 ItemReader 를 Lazy 하게 생성
    public ReverseJpaPagingItemReader<Point> expirePointItemReader(PointRepository pointRepository,
                                                            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today) {
        return new ReverseJpaPagingItemReaderBuilder<Point>()
                .name("expirePointItemReader")
                .query(pageable -> pointRepository.findPointToExpire(today, pageable)) // JPQL -> QueryDSL 로 변경
                .pageSize(1) // 한 개의 페이지의 사이즈
                .sort(Sort.by(Sort.Direction.ASC, "id"))
                .build();
    }

    /**
     * ItemProcessor 구현:
     * 1. ItemReader 에서 기져온 포인트들은 만료 일자가 지났기 때문에 만료 상태로 바꿔줘야됨
     *  ->만료 상태로 바꿔주는 작업을 ItemProcessor 에서 구현
     * 2. 해당 포인트들이 담긴 PointWallet 에서 해당 Point 들의 금액을 PointWallet 의 적립 금액에서 빼기
     * 
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
     * ItemWriter 구현:
     * ItemProcessor 에서 값들을 업데이트 해준 Point 랑 PointWallet 을 다시 DB에 저장
     * 
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
