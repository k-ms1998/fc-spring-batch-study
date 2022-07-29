package com.points.PointsManagementProject.job.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 오늘 기준, 만료된 포인트인지 확인하기 위해 날짜 확인하기
 */
@Component
public class InputExpiredPointAlarmCriteriaDateStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        /**
         * 1. Job Parameter 에서 today 가져오기
         * 2. today - 1 한 값을 alarmCriteriaDate 라는 이름으로 StepExecutionContext 에 주입 ( 포인트 만료 날짜가 어제 였으면, 만료해야 되는 포인트 -> today - 1)
         */
        JobParameter todayParameter = stepExecution.getJobParameters().getParameters().get("today");
        if (todayParameter == null) {
            return;
        }

        LocalDate today = LocalDate.parse(String.valueOf(todayParameter.getValue())); // Job Parameter 에서 today 가져오기
        ExecutionContext context = stepExecution.getExecutionContext();
        context.put("alarmCriteriaDate", today.minusDays(1).format(DateTimeFormatter.ISO_DATE)); // (K, V)
        stepExecution.setExecutionContext(context);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

}
