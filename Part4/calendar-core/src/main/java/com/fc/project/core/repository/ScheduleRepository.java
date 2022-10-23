package com.fc.project.core.repository;

import com.fc.project.core.domain.entity.Schedule;
import com.fc.project.core.repository.custom.ScheduleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

    public List<Schedule> findAllByUserId(Long userId);
}
