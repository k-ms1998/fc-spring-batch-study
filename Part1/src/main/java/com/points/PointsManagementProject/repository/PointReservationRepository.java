package com.points.PointsManagementProject.repository;

import com.points.PointsManagementProject.domain.PointReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointReservationRepository extends JpaRepository<PointReservation, Long> {
}
