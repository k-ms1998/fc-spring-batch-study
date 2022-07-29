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
 * 곧(7일 후에) 만료 예정인 포인트인지 확인하기 위해 만료 예정 날짜 계산
 */
@Component
public class InputExpiringSoonPointAlarmCriteriaDateStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        /**
         * 1. Job Parameter 에서 today 가져오기
         * 2. today + 7 한 값을 alarmCriteriaDate 라는 이름으로 StepExecutionContext 에 주입 ( 7일 후에 만료 예정인 포인트)
         */
        JobParameter todayParameter = stepExecution.getJobParameters().getParameters().get("today");
        if (todayParameter == null) {
            return;
        }

        LocalDate today = LocalDate.parse(String.valueOf(todayParameter.getValue())); // Job Parameter 에서 today 가져오기
        ExecutionContext context = stepExecution.getExecutionContext();
        context.put("alarmCriteriaDate", today.plusDays(7).format(DateTimeFormatter.ISO_DATE)); // (K, V)
        stepExecution.setExecutionContext(context);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

}
