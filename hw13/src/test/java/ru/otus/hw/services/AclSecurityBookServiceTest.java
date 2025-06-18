package ru.otus.hw.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.security.configurations.AclConfig;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Acl enabled book service should: ")
@DataJpaTest
@Import({BookServiceImpl.class, AclConfig.class})
@ComponentScan("ru.otus.hw.mapper")
class AclSecurityBookServiceTest {

    private static final String USER_JOHN = "john";
    private static final String USER_MICHEL = "michel";


    @Autowired
    private BookService bookService;

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private static Stream<Arguments> provideUserAndExpectedAccessibleBookIds() {
        return Stream.of(
                Arguments.of(USER_JOHN, List.of(1L, 2L, 3L)),
                Arguments.of(USER_MICHEL, List.of(2L, 3L))
        );
    }

    @DisplayName("return book list for user: ")
    @ParameterizedTest(name = "{0} have access to books #: {1}")
    @MethodSource("provideUserAndExpectedAccessibleBookIds")
    void shouldReturnBooksForUser(String username, List<Long> expectedBookIds) {

        // https://stackoverflow.com/questions/360520/unit-testing-with-spring-security
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(
                        username,
                        "dummy_password",
                        "ROLE_USER")
        );

        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(expectedBookIds.size())
                .extracting(BookDto::id)
                .containsExactlyInAnyOrderElementsOf(expectedBookIds);
    }
}