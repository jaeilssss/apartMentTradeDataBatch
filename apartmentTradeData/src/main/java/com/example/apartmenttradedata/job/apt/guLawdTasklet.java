package com.example.apartmenttradedata.job.apt;

import com.example.apartmenttradedata.core.repository.LawdRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

/**
 * ExecutionContext에 저장할 데이터
 * 1. guLawdCd - 구 코드 -> 다음 스텝에서 활용할 값
 * 2. guLawdCdList - 구 코드 리스트
 * 3. itemCount - 남아있는 구 코드의 갯수
 */

@RequiredArgsConstructor
public class guLawdTasklet implements Tasklet {

    private final LawdRepository lawdRepository;
    private List<String> guLawdCdList;
    private int itemCount;

    private static final String KEY_GU_LAWD_CD = "guLawdCd";
    private static final String KEY_ITEM_COUNT = "itemCount";
    private static final String KEY_GU_LAWD_CD_List = "guLawdCdList";

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {


        ExecutionContext executionContext = getExecutionContext(chunkContext);
        //데이터가 있으면 다음 스텝을 실행하도록 하고, 데이터가 없으면 종료되도록 한다.
        //데이터가 있으면 -> CONTINUABLE

        initList(executionContext);
        initItemCount(executionContext);

        if (itemCount == 0) {
            stepContribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }

        itemCount--;
        //아래 코드는 오류 남
        // getJobExecutionContext 는 변경 할 수 없는 map임 읽는 용도 Put은 안됨
//        chunkContext.getStepContext().getJobExecutionContext().put(KEY_GU_LAWD_CD, guLawdCdList.get(itemCount));

        executionContext.putString(KEY_GU_LAWD_CD, guLawdCdList.get(itemCount));
        executionContext.putInt(KEY_ITEM_COUNT, itemCount);

        stepContribution.setExitStatus(new ExitStatus("CONTINUABLE"));
        return RepeatStatus.FINISHED;
    }

    private ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        return stepExecution.getJobExecution().getExecutionContext();
    }

    private void initList(ExecutionContext executionContext) {
        if (executionContext.containsKey(KEY_GU_LAWD_CD_List)) {
            guLawdCdList = (List<String>) executionContext.get(KEY_GU_LAWD_CD_List);
        } else {
            guLawdCdList = lawdRepository.findDistinctGuLawdCd();
            executionContext.put(KEY_GU_LAWD_CD_List, guLawdCdList);
            executionContext.putInt(KEY_ITEM_COUNT, guLawdCdList.size());
        }
    }

    private void initItemCount(ExecutionContext executionContext) {
        if (executionContext.containsKey(KEY_ITEM_COUNT)) {
            itemCount = executionContext.getInt(KEY_ITEM_COUNT);
        } else {
            itemCount = guLawdCdList.size();
        }
    }
}
