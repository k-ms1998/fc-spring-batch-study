package com.fc.housebatch.core.job.apt;

import com.fc.housebatch.core.repository.LawdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * ExecutionContext 에 저장할 데이터
 * 1. guLawdCd -> 구 코드 -> 다음 Step 에서 활용할 값
 * 2. guLawdCdList -> 구 코드 리스트
 * 3. itemCount -> 남아있는 구 코드의 갯수
 */
@RequiredArgsConstructor
public class GuLawdTasklet implements Tasklet {

    private final LawdRepository lawdRepository;

    private String GU_LAWD_CD_LIST = "guLawdCdList";
    private String GU_LAWD_CD = "guLawdCd";
    private String ITEM_COUNT = "itemCount";

    private List<String> guLawdCdList =  new ArrayList<>();
    private int itemCount = 0;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext jobExecutionContext = getExecutionContext(chunkContext);


        initExecutionContext(jobExecutionContext);
        initItemCount(jobExecutionContext);

        /**
         * 더이상 읽을 데이터가 없을때
         */
        if (itemCount == 0) {
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }

        itemCount--;
        /**
         * ExecutionContext 는 Key-Value 로 값을 저장함
         */
        jobExecutionContext.putString(GU_LAWD_CD, guLawdCdList.get(itemCount));
        jobExecutionContext.putInt(ITEM_COUNT, itemCount);

        /**
         * 데이터가 있으면 다음 Step 실행. 없으면 종료.
         * 데이터가 있으면 -> CONTINUABLE
         */
        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));

        return RepeatStatus.FINISHED;
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        /**
         * ExecutionContext 를 가져오기 위해 먼저 현재 Step 의 StepExecution  가져오기
         */
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        /**
         * 현재 Job 에서의 ExecutionContext 를 가져오기
         * -> ExecutionContext 를 통해서 Step 끼리 데이터를 주고 받음
         */
        return stepExecution.getJobExecution().getExecutionContext();
    }

    private void initExecutionContext(ExecutionContext executionContext){
        if (executionContext.containsKey(GU_LAWD_CD_LIST)) {
            guLawdCdList = (List<String>) executionContext.get(GU_LAWD_CD_LIST);
        } else {
            /**
             * Step 을 처음 실행하면 ExecutionContext 에 값들이 없기 때문에 값들을 저장 시켜주기
             */
            guLawdCdList = lawdRepository.findAllDistinctGuLawdCd();
            executionContext.put(GU_LAWD_CD_LIST, guLawdCdList);
            executionContext.putInt(ITEM_COUNT, guLawdCdList.size());
        }
    }

    private void initItemCount(ExecutionContext executionContext) {
        if (executionContext.containsKey(ITEM_COUNT)) {
            itemCount = executionContext.getInt(ITEM_COUNT);
        } else {
            itemCount = guLawdCdList.size();
        }
    }
}
