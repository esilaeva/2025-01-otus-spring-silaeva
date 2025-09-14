package ru.otus.hw.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobStateListener implements JobExecutionListener {


    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        log.info("Starting PostgreSQL to Mongo data migration job.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("PostgreSQL to Mongo data migration job finished with status: {}", jobExecution.getStatus());
    }
}
