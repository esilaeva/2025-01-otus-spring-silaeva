package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.utils.TestUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA based repository for working with authors")
@DataJpaTest
class JpaAuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @DisplayName("should load a list of all authors")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = authorRepository.findAll();
        var expectedAuthors = TestUtils.generateIndexesSequence(1, 3)
                .map(id -> testEntityManager.find(Author.class, id))
                .toList();

        assertThat(actualAuthors).containsExactlyInAnyOrderElementsOf(expectedAuthors);
    }

    private static Stream<Arguments> provideExpectedAuthors() {
        return TestUtils.generateIndexesSequence(1, 3).map(Arguments::of);
    }

    @DisplayName("should load the author by id")
    @ParameterizedTest(name = "Author id is: {0}")
    @MethodSource("provideExpectedAuthors")
    void shouldReturnCorrectAuthorById(long index) {
        var actualAuthor = authorRepository.findById(index);
        var expectedAuthor = testEntityManager.find(Author.class, index);

        assertThat(actualAuthor)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedAuthor);
    }

    @Test
    @DisplayName("should return empty when author on ID not found")
    void shouldReturnEmptyForInvalidId() {
        assertThat(authorRepository.findById(999L)).isEmpty();
    }
}