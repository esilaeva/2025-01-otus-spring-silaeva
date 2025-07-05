package ru.otus.hw.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                           Step authorTransfer,
                           Step genreTransfer,
                           Step bookTransfer,
                           JobStateListener jobStateListener) {

        return new JobBuilder("transferJob", jobRepository)
                .start(authorTransfer)
                .next(genreTransfer)
                .next(bookTransfer)
                .listener(jobStateListener)
                .build();
    }
}
