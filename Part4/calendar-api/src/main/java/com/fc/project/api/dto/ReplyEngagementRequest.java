package com.fc.project.api.dto;

import com.fc.project.core.domain.enums.RequestStatus;
import com.fc.project.core.exception.CalendarException;
import com.fc.project.core.exception.ErrorCode;
import lombok.*;

@Data
@NoArgsConstructor
public class ReplyEngagementRequest {

    private RequestStatus type;

}
