package com.fc.project.api.dto;

import com.fc.project.core.domain.enums.IntervalUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@AllArgsConstructor
public class NotificationCreateRequest {

    private final String title;
    private final LocalDateTime notifyAt;
    private final RepeatInfo repeatInfo;

    public List<LocalDateTime> createNotifyAtList() {
        if (this.repeatInfo == null) {
            return Collections.singletonList(this.notifyAt);
        }

        return IntStream.range(0, this.repeatInfo.getTimes())
                .mapToObj(i -> {
                    int intervalValue = this.repeatInfo.getInterval().getIntervalValue();
                    IntervalUnit intervalUnit = this.repeatInfo.getInterval().getIntervalUnit();

                    switch (intervalUnit) {
                        case DAY:
                            return this.notifyAt.plusDays(intervalValue * i);
                        case WEEK:
                            return this.notifyAt.plusWeeks(intervalValue * i);
                        case MONTH:
                            return this.notifyAt.plusMonths(intervalValue * i);
                        case YEAR:
                            return this.notifyAt.plusYears(intervalValue * i);
                        default:
                            throw new RuntimeException("Bad Request. Invalid Interval Unit.");
                    }
                }).collect(Collectors.toList());
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
