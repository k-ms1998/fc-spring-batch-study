package com.points.PointsManagementProject.repository.custom.cutomImpl;

import com.points.PointsManagementProject.domain.ExpiredPointSummary;
import com.points.PointsManagementProject.domain.Point;
import com.points.PointsManagementProject.domain.QExpiredPointSummary;
import com.points.PointsManagementProject.domain.QPoint;
import com.points.PointsManagementProject.repository.custom.PointRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDate;
import java.util.List;

public class PointRepositoryCustomImpl extends QuerydslRepositorySupport implements PointRepositoryCustom{

    public PointRepositoryCustomImpl() {
        super(Point.class);
    }


    /**
     * select
     * w.user_id,
     * sum(p.amount)
     * from point p
     * inner join point_wallet w
     * on p.point_wallet_id = w.id
     * where p.is_expired = 1
     * and p.is_used = 0
     * and p.expire_date = ‘2021-01-01’
     * group by p.point_wallet_id
     */
    @Override
    public Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable) {
        QPoint point = QPoint.point;
        /**
         * 1. Query 결과는 ExpiredPointSummary 로 받기 위해 select 에 ExpiredPointSummary 생성자로 받아서 개발
         * 2. QueryDsl 에서 생성자를 사용하기 위해서는 생성자에 @QueryProjection 애노테이션 추가
         */

        /**
         * Query 작성
         */
        JPQLQuery<ExpiredPointSummary> query = from(point)
                .select(
                        new QExpiredPointSummary(
                                point.pointWallet.userId,
                                point.amount.sum().coalesce(0L)
                        )
                ).where(point.isExpired.eq(true))
                .where(point.isUsed.eq(false))
                .where(point.expiredDate.eq(alarmCriteriaDate))
                .groupBy(point.pointWallet);

        /**
         * 실제 Query 에 Paging 한 결과 가져오기
         */
        List<ExpiredPointSummary> result = getQuerydsl().applyPagination(pageable, query).fetch();
        Long fetchCount = query.fetchCount();
        return new PageImpl<>(
                result, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), fetchCount);
    }

    /**
     * alarmCriteriaDate 날짜 이내에 만료 예정인 포인트들 조회하기
     *
     * @param alarmCriteriaDate
     * @param pageable
     * @return
     */
    @Override
    public Page<ExpiredPointSummary> sumBeforeCriteriaDate(LocalDate alarmCriteriaDate, Pageable pageable) {
        QPoint point = QPoint.point;

        JPQLQuery<ExpiredPointSummary> query = from(point)
                .select(
                        new QExpiredPointSummary(
                                point.pointWallet.userId,
                                point.amount.sum().coalesce(0L)
                        )
                ).where(point.isExpired.eq(false))
                .where(point.isUsed.eq(false))
                .where(point.expiredDate.loe(alarmCriteriaDate))
                .groupBy(point.pointWallet);

        /**
         * 실제 Query 에 Paging 한 결과 가져오기
         */
        List<ExpiredPointSummary> result = getQuerydsl().applyPagination(pageable, query).fetch();
        Long fetchCount = query.fetchCount();
        return new PageImpl<>(
                result, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), fetchCount);
    }

    @Override
    public Page<Point> findPointToExpire(LocalDate today, Pageable pageable) {
        QPoint point = QPoint.point;

        JPQLQuery<Point> query = from(point)
                .select(point)
                .where(point.expiredDate.lt(today), point.isUsed.eq(false), point.isExpired.eq(false));
        List<Point> points = getQuerydsl().applyPagination(pageable, query).fetch();
        Long count = query.fetchCount();

        return new PageImpl<>(points, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), count);
    }

}
