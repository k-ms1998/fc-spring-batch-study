package com.fc.project.api.dto;

import com.fc.project.core.domain.entity.Share;
import com.fc.project.core.domain.enums.RequestStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fc.project.core.domain.entity.Share.*;

@Data
@NoArgsConstructor
public class ReplyShareRequest {

    private RequestStatus type;

}
