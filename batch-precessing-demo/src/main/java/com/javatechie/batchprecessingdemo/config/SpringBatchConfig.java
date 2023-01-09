package com.javatechie.batchprecessingdemo.config;

import com.javatechie.batchprecessingdemo.entity.Customer;
import com.javatechie.batchprecessingdemo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilder jobBuilder;
    private StepBuilder stepBuilder;
    private CustomerRepository customerRepository;


    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));   //indicate source of csv file
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);  // to skip header row
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Customer> lineMapper() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();  // will extract data from csv file
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);   //is strict by default
        // false means lines with less tokens will be tolerated and padded with empty columns
        // and lines with more tokens will be truncated
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");  // set header

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();  // will map the value from lineTokenizer to customer class
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);  // indicate repository which will store data
        writer.setMethodName("save");    // call writer.save() to save data into database
        return writer;
    }


//    public Step step1() {
//        return stepBuilder.get("csv-step")
//                .<Customer, Customer>chunk(10)
//                .reader(reader())
//                .processor(processor())
//                .writer(writer())
//                .build();
//    }
    @Bean
    public Job sampleJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("sampleJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sampleStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }


//    @Bean
//    public Job launchJob() {
//        return jobBuilder("importCustomers", j)
//                .flow(step1())
//                .end()
//                .build();
//    }

}

