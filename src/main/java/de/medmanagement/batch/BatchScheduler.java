package de.medmanagement.batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    Job checkDrugStatusJob;

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Scheduled(cron = "${cron.expression}")
    public void scheduleCheckDrugStatusTask() throws Exception {
        long now = System.currentTimeMillis() / 1000;
        System.out.println("Schedule tasks using cron jobs - " + now);
        JobParameters jobParameters = new JobParametersBuilder().addString("time", LocalDateTime.now().toString()).toJobParameters();
        JobExecution execution = jobLauncher.run(checkDrugStatusJob, jobParameters);
    }
}
