package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import com.fc.project.core.repository.EngagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EngagementService {

    private final EngagementRepository engagementRepository;

    @Transactional
    public RequestStatus update(AuthUser authUser, Long engagementId, RequestStatus requestStatus) {
        return engagementRepository.findById(engagementId)
                .filter(e -> e.getRequestStatus().equals(RequestStatus.REQUESTED))
                .filter(e -> e.getAttendee().getId().equals(authUser.getId()))
                .map(e -> e.reply(requestStatus))
                .orElseThrow(() -> new CalendarException(ErrorCode.BAD_REQUEST))
                .getRequestStatus();

    }
}
