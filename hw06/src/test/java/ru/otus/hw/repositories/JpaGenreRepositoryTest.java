package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;
import ru.otus.hw.utils.TestUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA based repository for working with genres")
@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;


    @DisplayName("should load the list of all genres")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualGenres = jpaGenreRepository.findAll();
        var expectedGenres = TestUtils.generateIndexesSequence(1, 3)
                .map(id -> testEntityManager.find(Genre.class, id))
                .toList();

        assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }


    private static Stream<Arguments> provideExpectedGenres() {
        return TestUtils.generateIndexesSequence(1, 3).map(Arguments::of);
    }

    @DisplayName("should load the genre by id")
    @ParameterizedTest(name = "Genre id is: {0}")
    @MethodSource("provideExpectedGenres")
    void shouldReturnCorrectGenreById(Long index) {
        var actualGenre = jpaGenreRepository.findById(index);
        var expectedGenre = testEntityManager.find(Genre.class, index);

        assertThat(actualGenre)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedGenre);
    }

    @Test
    @DisplayName("should return empty when genre ID not found")
    void shouldReturnEmptyForInvalidId() {
        assertThat(jpaGenreRepository.findById(999L)).isEmpty();
    }
}