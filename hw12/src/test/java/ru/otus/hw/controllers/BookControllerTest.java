package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.DtoToDtoMapper;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.utils.TestUtils.createExpectedBook;


@DisplayName("Book controller ")
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(controllers = BookController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class BookControllerTest {

    private static final String ALL_BOOKS_PAGE = "/books";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Autowired
    private DtoToDtoMapper dtoToDtoMapper;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GenreService genreService;


    @DisplayName("Should return all books page")
    @Test
    void shouldReturnAllBooksPage() throws Exception {
        var expectedBooks = Stream.of(
                        createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"),
                        createExpectedBook(3L, "BookTitle_3", "Author_3", "Genre_3")
                )
                .map(entityToDtoMapper::bookToBookDto)
                .toList();

        when(bookService.findAll()).thenReturn(expectedBooks);

        mockMvc.perform(get(ALL_BOOKS_PAGE))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attribute("books", expectedBooks));
    }

    @DisplayName("Should return book create page")
    @Test
    void bookCreatedPage() throws Exception {
        List<AuthorDto> authorDtoList = Stream.of(
                        new Author(1L, "Author_1"),
                        new Author(2L, "Author_2"),
                        new Author(3L, "Author_3"))
                .map(entityToDtoMapper::authorToAuthorDto)
                .toList();

        List<GenreDto> genreDtoList = Stream.of(
                        new Genre(1L, "Genre_1"),
                        new Genre(2L, "Genre_2"))
                .map(entityToDtoMapper::genreToGenreDto)
                .toList();

        when(authorService.findAll()).thenReturn(authorDtoList);
        when(genreService.findAll()).thenReturn(genreDtoList);

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-create"))
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList));
    }

    @DisplayName("Should return book edit page")
    @Test
    void bookEditPage() throws Exception {

        List<AuthorDto> authorDtoList = Stream.of(
                        new Author(1L, "Author_1"),
                        new Author(2L, "Author_2"),
                        new Author(3L, "Author_3"))
                .map(entityToDtoMapper::authorToAuthorDto)
                .toList();

        List<GenreDto> genreDtoList = Stream.of(
                        new Genre(1L, "Genre_1"),
                        new Genre(2L, "Genre_2"))
                .map(entityToDtoMapper::genreToGenreDto)
                .toList();

        BookDto bookDto = entityToDtoMapper.bookToBookDto(
                createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"));

        when(bookService.findById(1L)).thenReturn(bookDto);

        when(authorService.findAll()).thenReturn(authorDtoList);
        when(genreService.findAll()).thenReturn(genreDtoList);

        mockMvc.perform(get("/book/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("book-edit"))
                .andExpect(model().attribute("book", dtoToDtoMapper.bookDtoToBookUpdateDto(bookDto)))
                .andExpect(model().attribute("authors", authorDtoList))
                .andExpect(model().attribute("genres", genreDtoList));
    }


    @DisplayName("Should redirect to all books page after create a new book")
    @Test
    void shouldReturnAllBooksPageAfterInsertNewBook() throws Exception {

        mockMvc.perform(post("/book")
                        .param("title", "New_Book_Title")
                        .param("authorId", "1")
                        .param("genreId", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ALL_BOOKS_PAGE));

        verify(bookService, times(1)).create(
                new BookCreateDto("New_Book_Title", 1L, 3L)
        );
    }

    @DisplayName("Should redirect to all books page after update a book")
    @Test
    void shouldReturnAllBooksPageAfterUpdateBook() throws Exception {

        mockMvc.perform(put("/book")
                        .param("id", "2")
                        .param("title", "New_Book_Title_1")
                        .param("authorId", "1")
                        .param("genreId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ALL_BOOKS_PAGE));

        verify(bookService, times(1))
                .update(
                        new BookUpdateDto(2L, "New_Book_Title_1", 1L, 2L)
                );
    }

    @DisplayName("Should redirect to all books page after delete book")
    @Test
    void shouldReturnAllBooksPageAfterDeleteBook() throws Exception {
        mockMvc.perform(delete("/book/{id}", 4L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ALL_BOOKS_PAGE));

        verify(bookService, times(1)).deleteById(4L);
    }
}