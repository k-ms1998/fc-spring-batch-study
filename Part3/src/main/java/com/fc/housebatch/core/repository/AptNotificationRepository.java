package com.fc.housebatch.core.repository;

import com.fc.housebatch.core.entity.AptNotification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AptNotificationRepository extends JpaRepository<AptNotification, Long> {

    Page<AptNotification> findByEnabledIsTrue(Pageable pageable);
}
