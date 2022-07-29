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

    static private QPoint point = QPoint.point;

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
        List<ExpiredPointSummary> expiredPointList = from(point)
                .select(
                        new QExpiredPointSummary(
                                point.pointWallet.userId,
                                point.amount.sum().coalesce(0L)
                        )
                ).where(point.isExpired.eq(true))
                .where(point.isUsed.eq(false))
                .where(point.expiredDate.eq(alarmCriteriaDate))
                .groupBy(point.pointWallet)
                .fetch();

        return new PageImpl<>(
                expiredPointList, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), expiredPointList.size()
        );
    }

}
