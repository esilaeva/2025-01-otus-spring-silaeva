package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Genre rest controller should: ")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GenreRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ModelToDtoMapper modelToDtoMapper;


    @Test
    @DisplayName("return a list of all genres")
    void getAllGenres() {
        List<GenreDto> genreDtoList = TestUtils.genres.stream()
                .map(modelToDtoMapper::genreToGenreDto)
                .toList();

        webTestClient.get().uri("/api/v2/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class)
                .hasSize(genreDtoList.size())
                .value(actualGenreDtos -> assertThat(actualGenreDtos)
                        .containsExactlyInAnyOrderElementsOf(genreDtoList));
    }
}
