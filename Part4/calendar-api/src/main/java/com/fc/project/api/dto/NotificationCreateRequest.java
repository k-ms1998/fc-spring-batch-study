package com.fc.project.api.dto;

import com.fc.project.core.domain.enums.IntervalUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NotificationCreateRequest {

    private final String title;
    private final LocalDateTime notifyAt;
    private final RepeatInfo repeatInfo;

    public List<LocalDateTime> createNotifyAtList() {
        if (repeatInfo == null) {
            return Collections.singletonList(notifyAt);
        }

        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RepeatInfo {
        private final Interval interval;
        private final int times;

        public RepeatInfo of(int intervalValue, IntervalUnit intervalUnit, int times) {
            return new RepeatInfo(new Interval(intervalValue, intervalUnit), times);
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Interval{
        private final int intervalValue;
        private final IntervalUnit intervalUnit;
    }
}
