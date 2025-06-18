package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.configurations.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.utils.TestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfiguration.class)
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(controllers = BookController.class)
class BookControllerSecurityTest {

    private static final String LOGIN_PAGE = "http://localhost/login";
    private static final String MAIN_PAGE = "/books";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private void performRequestAndAssert(MockHttpServletRequestBuilder requestBuilder,
                                         @Nullable String userName,
                                         int expectedStatus,
                                         @Nullable String expectedRedirectUrl) throws Exception {
        if (userName != null) {
            requestBuilder.with(user(userName));
        }

        ResultActions resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));

        if (expectedRedirectUrl != null) {
            resultActions.andExpect(redirectedUrl(expectedRedirectUrl));
        }
    }

    @DisplayName("Should return expected status for all books page")
    @ParameterizedTest(name = "get /books method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllGetRequests")
    void shouldReturnExpectedStatusForAllBooksPage(@Nullable String userName,
                                                   int status,
                                                   @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/books");
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for book edit page")
    @ParameterizedTest(name = "get /book/1 method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllGetRequests")
    void shouldReturnExpectedStatusForBookEditPage(@Nullable String userName,
                                                   int status,
                                                   @Nullable String expectedRedirectUrl) throws Exception {

        var expectedBook = TestUtils.createExpectedBook(
                1, "Title_1", "Author_1", "Genre_1");
        var authorDtos = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> new Author(i, "Author_%s".formatted(i)))
                .map(entityToDtoMapper::authorToAuthorDto)
                .toList();
        List<GenreDto> genreDtos = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> new Genre(i, "Genre_%s".formatted(i)))
                .map(entityToDtoMapper::genreToGenreDto)
                .toList();

        when(bookService.findById(anyLong())).thenReturn(entityToDtoMapper.bookToBookDto(expectedBook));
        when(authorService.findAll()).thenReturn(authorDtos);
        when(genreService.findAll()).thenReturn(genreDtos);

        var requestBuilder = MockMvcRequestBuilders.get("/book/{id}", 1L);
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for book create page")
    @ParameterizedTest(name = "get /book method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllGetRequests")
    void shouldReturnExpectedStatusForBookCreatePage(@Nullable String userName,
                                                     int status,
                                                     @Nullable String expectedRedirectUrl) throws Exception {
        var authorDtos = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> new Author(i, "Author_%s".formatted(i)))
                .map(entityToDtoMapper::authorToAuthorDto)
                .toList();
        var genreDtos = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> new Genre(i, "Genre_%s".formatted(i)))
                .map(entityToDtoMapper::genreToGenreDto)
                .toList();

        when(authorService.findAll()).thenReturn(authorDtos);
        when(genreService.findAll()).thenReturn(genreDtos);

        var requestBuilder = MockMvcRequestBuilders.get("/book");
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for book updating attempt")
    @ParameterizedTest(name = "put /book method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForBookChanging(@Nullable String userName,
                                                   int status,
                                                   @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put("/book")
                .param("id", "2")
                .param("title", "New_Book_Title_1")
                .param("authorId", "1")
                .param("genreId", "2");
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for book creation attempt")
    @ParameterizedTest(name = "post /book method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForBookCreation(@Nullable String userName,
                                                   int status,
                                                   @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/book")
                .param("title", "New_Book_Title")
                .param("authorId", "1")
                .param("genreId", "3");
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for book deletion attempt")
    @ParameterizedTest(name = "delete /book/1 method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForBookDeletion(@Nullable String userName,
                                                   int status,
                                                   @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.delete("/book/{id}", 1L);
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    private static Stream<Arguments> provideTestDataForAllGetRequests() {
        return Stream.of(
                Arguments.of("user", 200, null),
                Arguments.of(null, 302, LOGIN_PAGE)
        );
    }

    private static Stream<Arguments> provideTestDataForAllOtherRequests() {
        return Stream.of(
                Arguments.of("user", 302, MAIN_PAGE),
                Arguments.of(null, 302, LOGIN_PAGE)
        );
    }
}
