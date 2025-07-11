package ru.otus.hw.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.job.processors.AuthorProcessor;
import ru.otus.hw.batch.job.processors.BookProcessor;
import ru.otus.hw.batch.job.processors.GenreProcessor;
import ru.otus.hw.mongo.model.MongoAuthor;
import ru.otus.hw.mongo.model.MongoBook;
import ru.otus.hw.mongo.model.MongoGenre;
import ru.otus.hw.postgres.model.Author;
import ru.otus.hw.postgres.model.Book;
import ru.otus.hw.postgres.model.Genre;

@Configuration
public class JobConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setThreadNamePrefix("parallel-flow-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public Step authorTransfer(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               JpaPagingItemReader<Author> authorReader,
                               AuthorProcessor authorProcessor,
                               MongoItemWriter<MongoAuthor> mongoAuthorWriter) {

        return new StepBuilder("authorTransfer", jobRepository)
                .<Author, MongoAuthor>chunk(3, transactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(mongoAuthorWriter)
                .build();
    }

    @Bean
    public Step genreTransfer(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JpaPagingItemReader<Genre> genreReader,
                              GenreProcessor genreProcessor,
                              MongoItemWriter<MongoGenre> mongoGenreWriter) {

        return new StepBuilder("genreTransfer", jobRepository)
                .<Genre, MongoGenre>chunk(3, transactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(mongoGenreWriter)
                .build();
    }

    @Bean
    public Flow authorFlow(Step authorTransfer) {
        return new FlowBuilder<SimpleFlow>("authorFlow")
                .start(authorTransfer)
                .build();
    }

    @Bean
    public Flow genreFlow(Step genreTransfer) {
        return new FlowBuilder<SimpleFlow>("genreFlow")
                .start(genreTransfer)
                .build();
    }

    @Bean
    public Flow authorGenreFlow(Flow authorFlow, Flow genreFlow) {
        return new FlowBuilder<SimpleFlow>("authorGenreFlow")
                .split(taskExecutor())
                .add(authorFlow, genreFlow)
                .build();
    }


    @Bean
    public Step bookTransfer(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             JpaPagingItemReader<Book> bookReader,
                             BookProcessor bookProcessor,
                             MongoItemWriter<MongoBook> mongoBookWriter) {

        return new StepBuilder("bookTransfer", jobRepository)
                .<Book, MongoBook>chunk(3, transactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(mongoBookWriter)
                .build();
    }

    @Bean
    public Job transferJob(JobRepository jobRepository,
                           Flow authorGenreFlow,
                           Step bookTransfer,
                           JobStateListener jobStateListener) {

        return new JobBuilder("transferJob", jobRepository)
                .start(authorGenreFlow)
                .next(bookTransfer)
                .build()
                .listener(jobStateListener)
                .build();
    }
}
