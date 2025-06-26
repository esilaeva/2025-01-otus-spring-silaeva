package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.security.configurations.SecurityConfiguration;
import ru.otus.hw.services.CommentService;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfiguration.class, H2ConsoleAutoConfiguration.class})
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(controllers = CommentController.class)
class CommentControllerSecurityTest {

    private static final String LOGIN_PAGE = "http://localhost/login";
    private static final String FIRST_BOOK_COMMENTS = "/book/1/comments";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;


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

    @DisplayName("Should return expected status for book comments page")
    @ParameterizedTest(name = "get /book/1/comments method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllGetRequests")
    void shouldReturnExpectedStatusForBookCommentsPage(@Nullable String userName,
                                                       int status,
                                                       @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/book/{bookId}/comments", 1L);
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for comment edit page")
    @ParameterizedTest(name = "get /comment/1 method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllGetRequests")
    void shouldReturnExpectedStatusForCommentEditPage(@Nullable String userName,
                                                      int status,
                                                      @Nullable String expectedRedirectUrl) throws Exception {
        when(commentService.findById(anyLong())).thenReturn(
                Optional.of(new CommentDto(1L, "Content", 1L)));
        var requestBuilder = MockMvcRequestBuilders.get("/comment/{id}", 1L);
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for comment creation attempt")
    @ParameterizedTest(name = "post /comment method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForCommentCreation(@Nullable String userName,
                                                      int status,
                                                      @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/comment")
                .param("content", "Content")
                .param("bookId", String.valueOf(1L));
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for comment updating attempt")
    @ParameterizedTest(name = "put /book/1/comment method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForCommentChanging(@Nullable String userName,
                                                      int status,
                                                      @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.put("/book/1/comment")
                .param("id", String.valueOf(1L))
                .param("content", "New_Content");
        performRequestAndAssert(requestBuilder, userName, status, expectedRedirectUrl);
    }

    @DisplayName("Should return expected status for comment deletion attempt")
    @ParameterizedTest(name = "delete /book/1/comment/1 method for user: {0} should return status: {1}")
    @MethodSource("provideTestDataForAllOtherRequests")
    void shouldReturnExpectedStatusForCommentDeletion(@Nullable String userName,
                                                      int status,
                                                      @Nullable String expectedRedirectUrl) throws Exception {
        var requestBuilder = MockMvcRequestBuilders
                .delete("/book/{bookId}/comment/{id}", 1L, 1L);
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
                Arguments.of("user", 302, FIRST_BOOK_COMMENTS),
                Arguments.of(null, 302, LOGIN_PAGE)
        );
    }
}
