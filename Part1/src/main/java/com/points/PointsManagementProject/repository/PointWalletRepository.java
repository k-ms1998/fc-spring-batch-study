package com.points.PointsManagementProject.repository;

import com.points.PointsManagementProject.domain.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointWalletRepository extends JpaRepository<PointWallet, Long> {
}
