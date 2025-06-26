package ru.otus.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.security.configurations.AclConfiguration;
import ru.otus.hw.security.services.AclServiceWrapperServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;


@DisplayName("Acl enabled book service should: ")
@DataJpaTest
@Import({BookServiceImpl.class, AclConfiguration.class, AclServiceWrapperServiceImpl.class})
@ComponentScan("ru.otus.hw.mapper")
class AclSecurityBookServiceTest {

    private static final String USER_JOHN = "john";

    private static final String USER_MICHEL = "michel";

    private static final String DUMMY_PASSWORD = "dummy_password";

    private static final String ROLE_USER = "ROLE_USER";


    @Autowired
    private BookService bookService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static Stream<Arguments> provideUserAndExpectedBookIds() {
        return Stream.of(
                Arguments.of(USER_JOHN, List.of(1L, 2L, 3L)),
                Arguments.of(USER_MICHEL, List.of(2L, 3L))
        );
    }

    @DisplayName("return book list for user: ")
    @ParameterizedTest(name = "{0} have access to books #: {1}")
    @MethodSource("provideUserAndExpectedBookIds")
    void shouldReturnBooksForUser(String username, List<Long> expectedBookIds) {

        // https://stackoverflow.com/questions/360520/unit-testing-with-spring-security
        setupAuthentication(username);

        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(expectedBookIds.size())
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(expectedBookIds);
    }

    @DisplayName("allow user to delete own book")
    @Test
    void shouldAllowUserToDeleteOwnBook() {
        var bookCreateDto = new BookCreateDto("John_New_Book", 1L, 1L);

        setupAuthentication(USER_JOHN);

        var newBook = bookService.create(bookCreateDto);

        // John can see all init books + the one created right now
        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(4)
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L, 3L, newBook.id()));

        // John also can delete his/hers book
        assertThatCode(() -> bookService.deleteById(newBook.id())).doesNotThrowAnyException();
        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(3)
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L, 3L));
    }

    @DisplayName("do not allow user to delete book that is not his/hers")
    @Test
    void shouldNotAllowUserToDeleteNotOwnBook() {
        var bookCreateDto = new BookCreateDto("John_New_Book", 1L, 1L);

        setupAuthentication(USER_JOHN);

        var newBook = bookService.create(bookCreateDto);

        // User John can see all init books + the one created right now
        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(4)
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(List.of(1L, 2L, 3L, newBook.id()));

        clearSecurityContext();

        setupAuthentication(USER_MICHEL);

        // Michel can see a new book + init books exclude the first due to authorization restrictions
        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(3)
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(List.of(2L, 3L, newBook.id()));

        // And can't delete book that is not his/hers
        assertThatCode(() -> bookService.deleteById(newBook.id()))
                .isInstanceOf(AuthorizationDeniedException.class);
    }


    @DisplayName("allow user to update own book")
    @Test
    void shouldAllowUserToUpdateOwnBook() {

        final String newTitle = "John's Updated Book";
        setupAuthentication(USER_JOHN);

        var bookCreateDto = new BookCreateDto("John_New_Book", 1L, 1L);
        var newBook = bookService.create(bookCreateDto);
        var updateDto = new BookUpdateDto(newBook.id(), newTitle, 1L, 1L);

        // John can update his/her book
        bookService.update(updateDto);
        var updatedBook = bookService.findById(newBook.id());
        assertThat(updatedBook.title()).isEqualTo(newTitle);
    }

    @DisplayName("do not allow user to update book that is not his/hers")
    @Test
    void shouldNotAllowUserToUpdateNotOwnBook() {

        setupAuthentication(USER_JOHN);
        var bookCreateDto = new BookCreateDto("John_New_Book", 1L, 1L);
        var newBook = bookService.create(bookCreateDto);

        var updateDto = new BookUpdateDto(newBook.id(), "Michel's Attempt", 1L, 1L);

        clearSecurityContext();

        setupAuthentication(USER_MICHEL);

        // Michel can see a new book created by John + init books exclude the first due to authorization restrictions
        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(3)
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(List.of(2L, 3L, newBook.id()));

        // But can't update book that is not his/hers
        assertThatCode(() -> bookService.update(updateDto))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    private static void setupAuthentication(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(username, DUMMY_PASSWORD, ROLE_USER)
        );
    }
}