package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.utils.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MongoDB based repository for working with Books: ")
@DataMongoTest
class MongoBookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @DisplayName("should load a list of all books")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = repository.findAll();
        var expectedAuthors = mongoTemplate.findAll(Book.class);

        assertThat(actualBooks)
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    @DisplayName("should load a book by id")
    @Test
    void shouldReturnCorrectBookById() {
        var secondBookId = TestUtils.books.get(1).getId();
        var expectedBook = mongoTemplate.findById(secondBookId, Book.class);
        var actualBook = repository.findById(secondBookId);

        assertThat(actualBook)
                .isNotEmpty()
                .contains(expectedBook);
    }
}
