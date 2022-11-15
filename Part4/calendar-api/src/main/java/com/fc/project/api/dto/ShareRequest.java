package com.fc.project.api.dto;

import com.fc.project.core.domain.entity.Share;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.fc.project.core.domain.entity.Share.*;

@Getter
@Setter
@AllArgsConstructor
public class ShareRequest {

    private final Long toUserId;
    private final Direction direction;
}
