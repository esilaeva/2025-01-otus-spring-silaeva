package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

// https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html#testing.spring-boot-applications.autoconfigured-spring-data-mongodb
@DisplayName("MongoDB based repository for working with Authors: ")
@DataMongoTest
class MongoAuthorRepositoryTest {

    @Autowired
    private AuthorRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @DisplayName("should load a list of all authors")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repository.findAll();
        var expectedAuthors = mongoTemplate.findAll(Author.class);

        assertThat(actualAuthors)
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    @Test
    @DisplayName("should return empty when author on Id not found")
    void shouldReturnEmptyForInvalidId() {

        assertThat(repository.findById("wrong_id")).isEmpty();
    }
}