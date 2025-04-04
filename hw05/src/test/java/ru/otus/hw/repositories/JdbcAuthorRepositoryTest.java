package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.utils.TestUtils.getDbAuthors;


@DisplayName("Jdbc based repository for working with authors")
@JdbcTest
@Import(JdbcAuthorRepository.class)
class JdbcAuthorRepositoryTest {

    @Autowired
    private JdbcAuthorRepository authorRepository;


    @DisplayName("should load a list of all authors")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = authorRepository.findAll();
        var expectedAuthors = getDbAuthors();

        assertThat(actualAuthors).containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    private static Stream<Arguments> provideExpectedAuthors() {
        return getDbAuthors().stream().map(Arguments::of);
    }

    @DisplayName("should load the author by id")
    @ParameterizedTest(name = "Author is: {0}")
    @MethodSource("provideExpectedAuthors")
    void shouldReturnCorrectAuthorById(Author expectedAuthor) {
        var actualAuthor = authorRepository.findById(expectedAuthor.getId());

        assertThat(actualAuthor)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("should return empty when author ID not found")
    void shouldReturnEmptyForInvalidId() {
        assertThat(authorRepository.findById(999L)).isEmpty();
    }
}
