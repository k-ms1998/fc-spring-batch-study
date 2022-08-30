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
 * ExecutionContext �� ������ ������
 * 1. guLawdCd -> �� �ڵ� -> ���� Step ���� Ȱ���� ��
 * 2. guLawdCdList -> �� �ڵ� ����Ʈ
 * 3. itemCount -> �����ִ� �� �ڵ��� ����
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
         * ���̻� ���� �����Ͱ� ������
         */
        if (itemCount == 0) {
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }

        itemCount--;
        /**
         * ExecutionContext �� Key-Value �� ���� ������
         */
        jobExecutionContext.putString(GU_LAWD_CD, guLawdCdList.get(itemCount));
        jobExecutionContext.putInt(ITEM_COUNT, itemCount);

        /**
         * �����Ͱ� ������ ���� Step ����. ������ ����.
         * �����Ͱ� ������ -> CONTINUABLE
         */
        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));

        return RepeatStatus.FINISHED;
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        /**
         * ExecutionContext �� �������� ���� ���� ���� Step �� StepExecution  ��������
         */
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

        /**
         * ���� Job ������ ExecutionContext �� ��������
         * -> ExecutionContext �� ���ؼ� Step ���� �����͸� �ְ� ����
         */
        return stepExecution.getJobExecution().getExecutionContext();
    }

    private void initExecutionContext(ExecutionContext executionContext){
        if (executionContext.containsKey(GU_LAWD_CD_LIST)) {
            guLawdCdList = (List<String>) executionContext.get(GU_LAWD_CD_LIST);
        } else {
            /**
             * Step �� ó�� �����ϸ� ExecutionContext �� ������ ���� ������ ������ ���� �����ֱ�
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
