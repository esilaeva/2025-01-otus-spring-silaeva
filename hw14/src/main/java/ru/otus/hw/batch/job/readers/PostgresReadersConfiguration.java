package ru.otus.hw.batch.job.readers;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.postgres.model.Author;
import ru.otus.hw.postgres.model.Book;
import ru.otus.hw.postgres.model.Genre;

@Configuration
public class PostgresReadersConfiguration {

    @Bean
    public JpaPagingItemReader<Author> authorReader(EntityManagerFactory postgresEntityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("postgresAuthorReader")
                .entityManagerFactory(postgresEntityManagerFactory)
                .queryString("SELECT a FROM Author a")
                .pageSize(3)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Book> bookReader(EntityManagerFactory postgresEntityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("postgresBookReader")
                .entityManagerFactory(postgresEntityManagerFactory)
                .queryString("SELECT b FROM Book b")
                .pageSize(3)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Genre> genreReader(EntityManagerFactory postgresEntityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("postgresGenreReader")
                .entityManagerFactory(postgresEntityManagerFactory)
                .queryString("SELECT g FROM Genre g")
                .pageSize(3)
                .build();
    }
}
