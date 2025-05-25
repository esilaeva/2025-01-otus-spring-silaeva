package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.utils.TestUtils;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GenreRestController ")
@WebMvcTest(GenreRestController.class)
class GenreRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("should return list of all genres")
    void shouldReturnAllGenreList() throws Exception {

        var allGenreDtoList = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> new GenreDto(i, "Genre_%s".formatted(i)))
                .toList();

        when(genreService.findAll()).thenReturn(allGenreDtoList);

        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(allGenreDtoList)));
    }
}