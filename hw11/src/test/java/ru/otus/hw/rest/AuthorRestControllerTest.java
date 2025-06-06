package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Author rest controller should: ")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ModelToDtoMapper modelToDtoMapper;


    @Test
    @DisplayName("return a list of all authors")
    void getAllAuthors() {
        List<AuthorDto> authorDtoList = TestUtils.authors.stream()
                .map(modelToDtoMapper::authorToAuthorDto)
                .toList();

        webTestClient.get().uri("/api/v2/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(authorDtoList.size())
                .value(actualAuthorDtos -> assertThat(actualAuthorDtos)
                        .containsExactlyInAnyOrderElementsOf(authorDtoList));
    }
}
