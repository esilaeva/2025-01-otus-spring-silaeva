package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.utils.TestUtils.getDbGenres;

@DisplayName("Jdbc based repository for working with genres")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository genreRepository;


    @DisplayName("should load the list of all genres")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualGenre = genreRepository.findAll();
        var expectedGenre = getDbGenres();
        assertThat(actualGenre).containsExactlyInAnyOrderElementsOf(expectedGenre);
    }

    private static Stream<Arguments> provideExpectedGenres() {
        return getDbGenres().stream().map(Arguments::of);
    }

    @DisplayName("should load the genre by id")
    @ParameterizedTest(name = "Genre is: {0}")
    @MethodSource("provideExpectedGenres")
    void shouldReturnCorrectGenreById(Genre expectedGenre) {
        var actualAuthor = genreRepository.findById(expectedGenre.getId());

        assertThat(actualAuthor)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedGenre);
    }

    @Test
    @DisplayName("should return empty when genre ID not found")
    void shouldReturnEmptyForInvalidId() {
        assertThat(genreRepository.findById(999L)).isEmpty();
    }
}
