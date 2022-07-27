package com.points.PointsManagementProject.repository;

import com.points.PointsManagementProject.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
