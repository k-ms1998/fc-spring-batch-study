package com.fc.project.api.dto;

import com.fc.project.core.util.Period;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class EngagementEmail {

    private static final String engagementUpdateApi = "http://localhost:8080/events/engagements/";
    private final Long engagementId;
    private final String recipient;
    private final List<String> attendeeEmails;
    private final String title;
    private final Period period;

    /**
     * Email 제목
     */
    public String getSubject() {
        return new StringBuilder()
                .append("[Invitation]")
                .append(this.title).append(" - ").append(this.period.toString())
                .append("(").append(this.recipient).append(")")
                .toString();
    }

    public Map<String, Object> getProperties() {
        final Map<String, Object> props = new HashMap<>();
        final String acceptUrl = new StringBuilder()
                .append(engagementUpdateApi).append(engagementId).append("?type=ACCEPTED").toString();
        final String rejectUrl = new StringBuilder()
                .append(engagementUpdateApi).append(engagementId).append("?type=REJECTED").toString();

        props.put("title", this.title);
        props.put("calendar", this.recipient);
        props.put("period", this.period.toString());
        props.put("attendees", this.attendeeEmails);
        props.put("acceptUrl", acceptUrl);
        props.put("rejectUrl", rejectUrl);

        return props;
    }

}
