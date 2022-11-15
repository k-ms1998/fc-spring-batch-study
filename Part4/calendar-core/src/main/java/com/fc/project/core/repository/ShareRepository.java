package com.fc.project.core.repository;

import com.fc.project.core.domain.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, Long> {
}
