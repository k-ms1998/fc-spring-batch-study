package com.points.PointsManagementProject.repository.custom;

import com.points.PointsManagementProject.domain.ExpiredPointSummary;
import com.points.PointsManagementProject.domain.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointRepositoryCustom {
    Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable);

    Page<ExpiredPointSummary> sumBeforeCriteriaDate(LocalDate alarmCriteriaDate, Pageable pageable);

    Page<Point> findPointToExpire(LocalDate today, Pageable pageable);
}
