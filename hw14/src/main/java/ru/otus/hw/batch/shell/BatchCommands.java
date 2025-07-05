package ru.otus.hw.batch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@ShellComponent
@RequiredArgsConstructor
public class BatchCommands {

    private final JobLauncher jobLauncher;

    private final Job transferJob;


    @ShellMethod(value = "start transfer", key = {"start", "st"})
    public String startTransfer() throws Exception {
        var jobExecution = jobLauncher.run(transferJob, new JobParametersBuilder()
                .addLong("timestamp", Instant.now().getEpochSecond())
                .toJobParameters());

        var duration = Duration.between(Objects.requireNonNull(jobExecution.getStartTime()), jobExecution.getEndTime());

        return "Job with id: %d - started at: %s, finished at: %s, duration is: %s milliseconds%n"
                .formatted(jobExecution.getId(),
                        jobExecution.getStartTime(),
                        jobExecution.getEndTime(),
                        TimeUnit.NANOSECONDS.toMillis(duration.get(ChronoUnit.NANOS)));
    }
}
