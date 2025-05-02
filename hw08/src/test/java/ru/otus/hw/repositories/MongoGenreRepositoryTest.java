package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MongoDB based repository for working with Genres: ")
@DataMongoTest
class MongoGenreRepositoryTest {

    @Autowired
    private GenreRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @DisplayName("should load a list of all genres")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualGenres = repository.findAll();
        var expectedGenres = mongoTemplate.findAll(Genre.class);

        assertThat(actualGenres)
                .isNotEmpty()
                .hasSize(6)
                .containsExactlyInAnyOrderElementsOf(expectedGenres);
    }
}