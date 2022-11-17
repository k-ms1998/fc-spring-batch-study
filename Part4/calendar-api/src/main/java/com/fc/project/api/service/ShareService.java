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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fc.project.core.domain.entity.Share.*;

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

        shareRepository.save(builder()
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

    /**
     * Calendar 를 경우하고 있는 대상들을 조회
     * 1. 자신과 양방향 공유관계인 상대방 -> 공유하는 대상(from)이 me 이면 공유 받은 대상의 userId || 공유받는 대상(to)이 me 이면 공유 해준 대상의 userId
     * 2. 단방향 공유관계인 경우 공유받은 대상이 me 일때, 굥유 해준 사람의 userId
     * 1번 결과 + 2번 결과 + 자기 자신의 userId 를 반환
     */
    public List<Long> findSharedUserIdsByUser(AuthUser authUser) {

        final Stream<Long> sharedWithMeBiDirectional
                = shareRepository.findSharedWithMeBiDirectional(authUser.getId(), RequestStatus.ACCEPTED, Direction.BI_DIRECTIONAL)
                .stream()
                .map(s -> s.getToUserId().equals(authUser.getId()) ? s.getFromUserId() : s.getToUserId());
        final Stream<Long> sharedWithMeUniDirectional
                = shareRepository.findSharedWithMeUniDirectional(authUser.getId(), RequestStatus.ACCEPTED, Direction.UNI_DIRECTIONAL)
                .stream()
                .map(s -> s.getFromUserId());


        return Stream.concat(Stream.of(authUser.getId()), Stream.concat(sharedWithMeBiDirectional, sharedWithMeUniDirectional))
                .collect(Collectors.toList());
    }
}
