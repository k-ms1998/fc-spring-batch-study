package com.fc.project.core.repository.custom.impl;

import com.fc.project.core.domain.entity.Engagement;
import com.fc.project.core.repository.custom.EngagementRepositoryCustom;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class EngagementRepositoryCustomImpl implements EngagementRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Engagement> findByAttendeeId(Long attendeeId) {
        return em.createNativeQuery("select *from engagement eg where eg.attendee_id = " + attendeeId, Engagement.class)
                .getResultList();
    }
}
