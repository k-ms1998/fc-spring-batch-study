package com.fc.project.api.service;

import com.fc.project.api.dto.AuthUser;
import com.fc.project.api.dto.ShareRequest;
import com.fc.project.core.domain.entity.Share;
import com.fc.project.core.domain.entity.User;
import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import com.fc.project.core.repository.ScheduleRepository;
import com.fc.project.core.repository.ShareRepository;
import com.fc.project.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {

    private final UserService userService;
    private final EmailService emailService;
    private final ShareRepository shareRepository;

    @Transactional
    public void share(AuthUser authUser, ShareRequest request) {
        final User fromUser = userService.findByUserIdOrThrow(authUser.getId());
        final User toUser = userService.findByUserIdOrThrow(request.getToUserId());

        shareRepository.save(Share.builder()
                .fromUserId(fromUser.getId())
                .toUserId(toUser.getId())
                .direction(request.getDirection())
                .requestStatus(RequestStatus.REQUESTED)
                .build());

        emailService.sendShareRequestMail(fromUser.getEmail(), toUser.getName(), request.getDirection());
    }

    @Transactional
    public void replyToRequest(AuthUser toAuthUser, Long shareId, RequestStatus type) {
        shareRepository.findById(shareId)
                .filter(share -> share.getToUserId().equals(toAuthUser.getId()))
                .filter(share -> share.getRequestStatus() == RequestStatus.REQUESTED)
                .map(share -> share.reply(type))
                .orElseThrow(() -> new CalendarException(ErrorCode.BAD_REQUEST));
    }
}
