package com.fc.project.core.repository;

import com.fc.project.core.domain.entity.Share;
import com.fc.project.core.domain.entity.Share.Direction;
import com.fc.project.core.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShareRepository extends JpaRepository<Share, Long> {

    @Query("SELECT s FROM Share s WHERE s.requestStatus = :accepted AND s.direction = :biDirectional AND (s.fromUserId = :userId OR s.toUserId = :userId)")
    List<Share> findSharedWithMeBiDirectional(Long userId, RequestStatus accepted, Direction biDirectional);

    @Query("SELECT s FROM Share s WHERE s.requestStatus = :accepted AND s.direction = :uniDirectional AND s.toUserId = :userId")
    List<Share> findSharedWithMeUniDirectional(Long userId, RequestStatus accepted, Direction uniDirectional);
}
