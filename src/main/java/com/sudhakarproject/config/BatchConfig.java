package com.sudhakarproject.config;

import com.sudhakarproject.joblistener.JobListener;
import com.sudhakarproject.pojo.Transactions;
import com.sudhakarproject.stepsummarylistener.StepSummaryListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {


    private final ItemReader<Transactions> reader;
    private final ItemProcessor<Transactions, Transactions> processor;
    private final ItemWriter<Transactions> writer;
    private final StepSummaryListener listener;




    @Bean
    public Job job(JobRepository jobRepository, Step step, JobListener listener) {
        return new JobBuilder("oracle-extract-job", jobRepository)
                .listener(listener)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository){
        return  new StepBuilder("step", jobRepository)
                .<Transactions, Transactions>chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();
    }
}
