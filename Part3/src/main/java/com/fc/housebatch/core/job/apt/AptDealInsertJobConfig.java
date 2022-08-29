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
                .next(executionContextPrintStep)
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

            List<String> allDistinctGuLawdCd = lawdRepository.findAllDistinctGuLawdCd();
            /**
             * ExecutionContext �� Key-Value �� ���� ������
             */
            jobExecutionContext.putString("guLawdCd", allDistinctGuLawdCd.get(0));

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public StaxEventItemReader<AptDealDto> aptDealResourceReader(Jaxb2Marshaller aptDealDtoMarshaller,
                                                                 @Value("#{jobParameters['yearMonth']}") String yearMonthStr,
                                                                 @Value("#{jobParameters['lawdCd']}") String lawdCd) {
        return new StaxEventItemReaderBuilder<AptDealDto>()
                .name("aptDealResourceReader")
                .resource(apartmentApiResource.getResource(lawdCd, YearMonth.parse(yearMonthStr)))
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