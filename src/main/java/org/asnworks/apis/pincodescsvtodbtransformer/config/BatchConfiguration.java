
package org.asnworks.apis.pincodescsvtodbtransformer.config;

import javax.sql.DataSource;

import org.asnworks.apis.pincodescsvtodbtransformer.listener.JobCompletionNotificationListener;
import org.asnworks.apis.pincodescsvtodbtransformer.model.PinCode;
import org.asnworks.apis.pincodescsvtodbtransformer.processor.PinCodeProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author sudambat
 */
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class BatchConfiguration {

    public static final String STEP_ONE = "step1";
    public static final String JOB_NAME = "importPinCodesJob";
    public static final String PINCODE_CSV_FILE = "Locality_village_pincode_final_mar-2017.csv";
    public static final String INSERT_QUERY =
        "INSERT INTO pincode (village_name, office_name, pin_code, sub_district_name, district_name, state_name) VALUES (:villageName, :officeName, :code, :subDistrictName, :districtName, :stateName)";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public FlatFileItemReader<PinCode> reader() {
        FlatFileItemReader<PinCode> reader = new FlatFileItemReader<PinCode>();
        reader.setResource(new ClassPathResource(PINCODE_CSV_FILE));
        reader.setLineMapper(new DefaultLineMapper<PinCode>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] {"villageName", "officeName", "code", "subDistrictName", "districtName", "stateName"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<PinCode>() {
                    {
                        setTargetType(PinCode.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public PinCodeProcessor processor() {
        return new PinCodeProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<PinCode> writer() {
        JdbcBatchItemWriter<PinCode> writer = new JdbcBatchItemWriter<PinCode>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<PinCode>());
        writer.setSql(INSERT_QUERY);
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importPinCodesJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JOB_NAME)
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1())
            .end()
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get(STEP_ONE)
            .<PinCode, PinCode>chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    //

    @Bean
    public ResourcelessTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager txManager)
        throws Exception {
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public JobRepository jobRepository(MapJobRepositoryFactoryBean factory) throws Exception {
        return factory.getObject();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        return launcher;
    }

}
