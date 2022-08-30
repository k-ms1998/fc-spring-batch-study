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
                     * getGuLawdCdStep ���������� ����(���� ó������ ���� �����Ͱ� ������)
                     * -> CONTINUABLE(on) -> executionContextPrintStep ����(to) -> �ٽ� getGuLawdCdStep �� ���� ���� ������ ��������(next)
                     */
                    .on("CONTINUABLE").to(executionContextPrintStep).next(getGuLawdCdStep)
                    .from(getGuLawdCdStep).on("*").end() // getGuLawdCdStep ���������� ����(��� �����͸� �����ͼ� ó��������) -> ����
                    .end()// FlowJobBuilder end
//                .next(executionContextPrintStep)
//                .next(aptDealInsertStep)
                .build();
    }

    /**
     * CompositeJobParametersValidator ���� setValidators �� validator ���� ��ȸ
     * ��ȸ�ϸ鼭 �� validator �� validate ȣ��
     * validate �� ȣ��Ǹ鼭 JobParameter Ȯ��
     * �̶�, setValidators �� ����Ʈ�� �Ѱ���
     *  -> ����Ʈ�� ������� validate �� ȣ���ؼ� Ȯ����
     *      -> �� �������� ���� �߻��ϸ�, �ڿ� �ִ� validator ���� Ȯ�� X
     *          -> A, B, C ������ validator ���� ������, A���� ������ �߻��ϸ� B, C �� Ȯ�� X
     */
    @Bean
    protected JobParametersValidator aptDealJobParameterValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(
                /**
                 * getGuLawdCdStep �� jobExecutionContext �� lawdCd/guLawdCd �� �������� ��쿡�� LawdCdParameterValidator �ʿ� X
                 * LawdCdParameterValidator �� jobParameter �� lawdCd �� �����ö� validate �ϴ� ���� ����
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
         * getGuLawdCdStep -> guLawdCdTasklet (ExecutionContext �� ������ ����)
         *                      -> executionContextPrintStep -> tasklet ���� ExecutionContext ���� �ռ� ������ �����͸� �����ͼ� ���
         */
        return stepBuilderFactory
                .get("executionContextPrintStep")
                .tasklet((contribution, chunkContext) -> {
                    /**
                     * ExecutionContext �� ����� ���� �����ͼ� ����ϱ�
                     */
                    ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    String guLawdCd = executionContext.getString("guLawdCd");
                    System.out.println("[executionContextPrintStep Tasklet] guLawdCd = " + guLawdCd);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    /**
     * ExecutionContext �� ������ ������
     * 1. guLawdCd -> �� �ڵ� -> ���� Step ���� Ȱ���� ��
     * 2. guLawdCdList -> �� �ڵ� ����Ʈ
     * 3. itemCount -> �����ִ� �� �ڵ��� ����
     * @return
     */
    @Bean
    @StepScope
    public Tasklet guLawdCdTasklet() {
        return (contribution, chunkContext) -> {
            /**
             * ExecutionContext �� �������� ���� ���� ���� Step �� StepExecution  ��������
             */
            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

            /**
             * ���� Job ������ ExecutionContext �� ��������
             * -> ExecutionContext �� ���ؼ� Step ���� �����͸� �ְ� ����
             */
            ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();

            List<String> guLawdCdList;
            if (!jobExecutionContext.containsKey("guLawdCdList")) {
                /**
                 * Step �� ó�� �����ϸ� ExecutionContext �� ������ ���� ������ ������ ���� �����ֱ�
                 */
                guLawdCdList = lawdRepository.findAllDistinctGuLawdCd();
                jobExecutionContext.put("guLawdCdList", guLawdCdList);
                jobExecutionContext.putInt("itemCount", guLawdCdList.size());
            } else {
                guLawdCdList = (List<String>) jobExecutionContext.get("guLawdCdList");
            }
            Integer itemCount = jobExecutionContext.getInt("itemCount");

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
            String guLawdCd = guLawdCdList.get(itemCount);
            jobExecutionContext.putString("guLawdCd", guLawdCd);
            jobExecutionContext.putInt("itemCount", itemCount);

            /**
             * �����Ͱ� ������ ���� Step ����. ������ ����.
             * �����Ͱ� ������ -> CONTINUABLE
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
                .addFragmentRootElements("item") // ���� Element ����; xml ���Ͽ��� <item> �±� �б�
                .unmarshaller(aptDealDtoMarshaller) // ����(apartment-api-response.xml)�� ��ü(AptDealDto)�� �����ϱ�
                .build();
    }

    @Bean
    @StepScope
    public Jaxb2Marshaller aptDealDtoMarshaller() {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        /**
         * AptDealDto �� �����ϱ�
         */
        jaxb2Marshaller.setClassesToBeBound(AptDealDto.class);
        return jaxb2Marshaller;
    }

}
