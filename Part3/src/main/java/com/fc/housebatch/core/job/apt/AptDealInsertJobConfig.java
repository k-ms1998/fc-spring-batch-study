package com.fc.housebatch.core.job.apt;

import ch.qos.logback.core.joran.event.stax.StaxEvent;
import com.fc.housebatch.adapter.ApartmentApiResource;
import com.fc.housebatch.core.dto.AptDealDto;
import com.fc.housebatch.core.job.validator.FilePathParameterValidator;
import com.fc.housebatch.core.job.validator.LawdCdParameterValidator;
import com.fc.housebatch.core.job.validator.YearMonthParameterValidator;
import com.fc.housebatch.core.repository.LawdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AptDealInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ApartmentApiResource apartmentApiResource;
    private final LawdRepository lawdRepository;

    @Bean
    public Job aptDealInsertJob(Step aptDealInsertStep, Step getGuLawdCdStep, Step executionContextPrintStep,
                                JobParametersValidator aptDealJobParameterValidator) {
        return jobBuilderFactory
                .get("aptDealInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(aptDealJobParameterValidator)
                .start(getGuLawdCdStep)
                    /**
                     * getGuLawdCdStep 실행했을때 성공(아직 처리하지 않은 데이터가 있을때)
                     * -> CONTINUABLE(on) -> executionContextPrintStep 실행(to) -> 다시 getGuLawdCdStep 로 가서 다음 데이터 가져오기(next)
                     */
                    .on("CONTINUABLE").to(executionContextPrintStep).next(getGuLawdCdStep)
                    .from(getGuLawdCdStep).on("*").end() // getGuLawdCdStep 실행했을때 실패(모든 데이터를 가져와서 처리했을때) -> 종료
                    .end()// FlowJobBuilder end
//                .next(executionContextPrintStep)
//                .next(aptDealInsertStep)
                .build();
    }

    /**
     * CompositeJobParametersValidator 에서 setValidators 한 validator 들을 순회
     * 순회하면서 각 validator 의 validate 호출
     * validate 이 호출되면서 JobParameter 확인
     * 이때, setValidators 를 리스트로 넘겨줌
     *  -> 리스트의 순서대로 validate 을 호출해서 확인함
     *      -> 앞 순서에서 에러 발생하면, 뒤에 있는 validator 들은 확인 X
     *          -> A, B, C 순서로 validator 들이 있을때, A에서 에러가 발생하면 B, C 는 확인 X
     */
    @Bean
    protected JobParametersValidator aptDealJobParameterValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(
                /**
                 * getGuLawdCdStep 로 jobExecutionContext 로 lawdCd/guLawdCd 를 가져오는 경우에는 LawdCdParameterValidator 필요 X
                 * LawdCdParameterValidator 는 jobParameter 로 lawdCd 를 가져올때 validate 하는 것이 목적
                 */
                new LawdCdParameterValidator(),
                new YearMonthParameterValidator()
        ));

        return validator;
    }

    @Bean
    @JobScope
    public Step aptDealInsertStep(StaxEventItemReader<AptDealDto> aptDealResourceReader) {
        return stepBuilderFactory
                .get("aptDealInsertStep")
                .<AptDealDto, AptDealDto>chunk(10)
                .reader(aptDealResourceReader)
                .writer(new ItemWriter<AptDealDto>() {
                    @Override
                    public void write(List<? extends AptDealDto> items) throws Exception {
                        items.forEach(i -> System.out.println(i));
                        System.out.println("===== Chunk Completed =====");
                    }
                })
                .build();
    }

    @Bean
    @JobScope
    public Step getGuLawdCdStep(Tasklet guLawdCdTasklet) {
        return stepBuilderFactory
                .get("getGuLawdCdStep")
                .tasklet(guLawdCdTasklet)
                .build();
    }

    @Bean
    @JobScope
    public Step executionContextPrintStep(){
        /**
         * getGuLawdCdStep -> guLawdCdTasklet (ExecutionContext 에 데이터 저장)
         *                      -> executionContextPrintStep -> tasklet 에서 ExecutionContext 에서 앞서 저장한 데이터를 가져와서 출력
         */
        return stepBuilderFactory
                .get("executionContextPrintStep")
                .tasklet((contribution, chunkContext) -> {
                    /**
                     * ExecutionContext 에 저장된 값을 가져와서 출력하기
                     */
                    ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    String guLawdCd = executionContext.getString("guLawdCd");
                    System.out.println("[executionContextPrintStep Tasklet] guLawdCd = " + guLawdCd);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    /**
     * ExecutionContext 에 저장할 데이터
     * 1. guLawdCd -> 구 코드 -> 다음 Step 에서 활용할 값
     * 2. guLawdCdList -> 구 코드 리스트
     * 3. itemCount -> 남아있는 구 코드의 갯수
     * @return
     */
    @Bean
    @StepScope
    public Tasklet guLawdCdTasklet() {
        return (contribution, chunkContext) -> {
            /**
             * ExecutionContext 를 가져오기 위해 먼저 현재 Step 의 StepExecution  가져오기
             */
            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

            /**
             * 현재 Job 에서의 ExecutionContext 를 가져오기
             * -> ExecutionContext 를 통해서 Step 끼리 데이터를 주고 받음
             */
            ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();

            List<String> guLawdCdList;
            if (!jobExecutionContext.containsKey("guLawdCdList")) {
                /**
                 * Step 을 처음 실행하면 ExecutionContext 에 값들이 없기 때문에 값들을 저장 시켜주기
                 */
                guLawdCdList = lawdRepository.findAllDistinctGuLawdCd();
                jobExecutionContext.put("guLawdCdList", guLawdCdList);
                jobExecutionContext.putInt("itemCount", guLawdCdList.size());
            } else {
                guLawdCdList = (List<String>) jobExecutionContext.get("guLawdCdList");
            }
            Integer itemCount = jobExecutionContext.getInt("itemCount");

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
            String guLawdCd = guLawdCdList.get(itemCount);
            jobExecutionContext.putString("guLawdCd", guLawdCd);
            jobExecutionContext.putInt("itemCount", itemCount);

            /**
             * 데이터가 있으면 다음 Step 실행. 없으면 종료.
             * 데이터가 있으면 -> CONTINUABLE
             */
            contribution.setExitStatus(new ExitStatus("CONTINUABLE"));

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(Jaxb2Marshaller aptDealDtoMarshaller,
                                                                 @Value("#{jobParameters['yearMonth']}") String yearMonthStr,
                                                                 @Value("#{jobExecutionContext['guLawdCd']}") String guLawdCd) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartmentApiResource.getResource(guLawdCd, YearMonth.parse(yearMonthStr)))
                .addFragmentRootElements("item") // 읽을 Element 설정; xml 파일에서 <item> 태그 읽기
                .unmarshaller(aptDealDtoMarshaller) // 파일(apartment-api-response.xml)을 객체(AptDealDto)에 매핑하기
                .build();
    }

    @Bean
    @StepScope
    public Jaxb2Marshaller aptDealDtoMarshaller() {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        /**
         * AptDealDto 로 매핑하기
         */
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }

}
