package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.*;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.services.BookService;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.utils.TestUtils.createExpectedBook;


@DisplayName("Book rest controller ")
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @MockitoBean
    private BookService bookService;

    @DisplayName("Should return all books page")
    @Test
    void allBooks() throws Exception {
        var allBookDtoList = Stream.of(
                        createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"),
                        createExpectedBook(3L, "BookTitle_3", "Author_3", "Genre_3")
                )
                .map(entityToDtoMapper::bookToBookDto)
                .toList();

        when(bookService.findAll()).thenReturn(allBookDtoList);

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(allBookDtoList)));
    }


    @DisplayName("Should create a new book")
    @Test
    void createBook() throws Exception {
        var bookCreateDto = new BookCreateDto("New_Book_Title", 2L, 2L);
        var bookDto = new BookDto(4L, "New_Book_Title",
                new AuthorDto(1L, "Author_1"),
                new GenreDto(1L, "Genre_1"));

        when(bookService.create(bookCreateDto)).thenReturn(bookDto);

        mockMvc.perform(post("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)));
    }


    @DisplayName("Should update a book")
    @Test
    void updateBook() throws Exception {
        var bookUpdateDto = new BookUpdateDto(1L, "New_Book_Title", 2L, 2L);
        var bookDto = new BookDto(1L, "New_Book_Title",
                new AuthorDto(2L, "Author_2"),
                new GenreDto(2L, "Genre_2"));

        when(bookService.update(bookUpdateDto)).thenReturn(bookDto);

        mockMvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)));

    }

    @DisplayName("Should delete a book")
    @Test
    void returnSingleBook() throws Exception {

        mockMvc.perform(delete("/api/v1/book/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(1L);
    }

    @DisplayName("Should return 404 error if book is not found")
    @Test
    void returnNotFoundError() throws Exception {

        when(bookService.findById(1L)).thenThrow(
                new EntityNotFoundException(
                        NotFoundMessage.BOOK.getMessage().formatted(1L))
        );
        var errorDto = new ErrorDto(404, NotFoundMessage.BOOK.getMessage().formatted(1L));

        mockMvc.perform(get("/api/v1/book/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(errorDto)));
    }
}