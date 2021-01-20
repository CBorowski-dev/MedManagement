package de.medmanagement.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CheckDrugStatusTask checkDrugStatusTask;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(checkDrugStatusTask)
                .build();
    }

    @Bean
    public Job checkDrugStatusJob(JobCompletionNotificationListener listener){
        return jobBuilderFactory.get("CheckDrugStatusJob")
                .incrementer(new RunIdIncrementer())
                //.listener(listener)
                .start(step1())
                .build();
    }

}
