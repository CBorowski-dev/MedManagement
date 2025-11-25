package de.medmanagement.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Tasklet myCheckDrugStatusTask() {
        return new CheckDrugStatusTask();
    }

    @Bean
    public Step checkDrugStatusTask_Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("checkDrugStatusTask", jobRepository)
                .tasklet(myCheckDrugStatusTask(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet myCheckDrugConsumptionTask() {
        return new CheckDrugConsumptionTask();
    }

    @Bean
    public Step checkDrugConsumptionTask_Step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("checkDrugConsumptionTask", jobRepository)
                .tasklet(myCheckDrugConsumptionTask(), transactionManager)
                .build();
    }

    @Bean
    public Job checkDrugStatusJob(@Qualifier("checkDrugStatusTask_Step1") Step step, JobRepository jobRepository){
        System.out.println("--> in BatchConfiguration#checkDrugStatusJob(JobCompletionNotificationListener)");
        return new JobBuilder("CheckDrugStatusJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                // .listener(listener) // not necessary here
                .start(step)
                .build();
    }

    @Bean
    public Job checkDrugConsumptionJob(@Qualifier("checkDrugConsumptionTask_Step1") Step step, JobRepository jobRepository){
        System.out.println("--> in BatchConfiguration#checkDrugConsumptionJob(JobCompletionNotificationListener)");
        return new JobBuilder("CheckDrugConsumptionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                // .listener(listener) // not necessary here
                .start(step)
                .build();
    }
}
