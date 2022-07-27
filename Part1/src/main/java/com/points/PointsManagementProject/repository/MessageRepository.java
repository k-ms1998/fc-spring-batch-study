package com.points.PointsManagementProject.repository;

import com.points.PointsManagementProject.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
