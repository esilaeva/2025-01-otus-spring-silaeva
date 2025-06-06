package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Comment rest controller should: ")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ModelToDtoMapper modelToDtoMapper;


    @Test
    @DisplayName("return a comment by id")
    void getCommentByCommentId() {
        Comment firstComment = TestUtils.comments.get(0);

        webTestClient.get().uri("/api/v2/comment/{id}", firstComment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .isEqualTo(modelToDtoMapper.commentToCommentDto(firstComment));
    }

    @Test
    @DisplayName("return a list of comments for a given book")
    void getCommentByBookId() {
        Book firstBook = TestUtils.books.get(0);
        List<CommentDto> commentDtoList = TestUtils.comments.stream()
                .filter(comment -> comment.getBook().equals(firstBook))
                .map(modelToDtoMapper::commentToCommentDto)
                .toList();

        webTestClient.get().uri("/api/v2/book/{bookId}/comment", firstBook.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(commentDtoList.size())
                .value(actualCommentDtos -> assertThat(actualCommentDtos)
                        .containsExactlyInAnyOrderElementsOf(commentDtoList));
    }

    @Test
    @DisplayName("insert a new comment for an existing book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertNewCommentForExistingBook() {

        Book firstBook = TestUtils.books.get(0);
        CommentCreateDto commentCreateDto = new CommentCreateDto(firstBook.getId(),
                "New comment content");

        webTestClient.post().uri("/api/v2/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(commentCreateDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .value(commentDto -> {
                    assertThat(commentDto.id()).isNotNull().isNotBlank();
                    assertThat(commentDto)
                            .extracting(CommentDto::content)
                            .isEqualTo("New comment content");
                });
    }

    @Test
    @DisplayName("update an existing comment")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateComment() {
        Comment commentToUpdate = TestUtils.comments.get(0);
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(commentToUpdate.getId(),
                "Updated comment content");

        webTestClient.put().uri("/api/v2/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(commentUpdateDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CommentDto.class)
                .value(updatedComment -> {
                    assertThat(updatedComment.id()).isEqualTo(commentToUpdate.getId());
                    assertThat(updatedComment.content()).isEqualTo("Updated comment content");
                });
    }

    @Test
    @DisplayName("delete an existing comment")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteCommentByCommentId() {
        Book book = TestUtils.books.get(0);
        Comment commentToDelete = TestUtils.comments.get(0);

        webTestClient.delete().uri("/api/v2/comment/{id}", commentToDelete.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        List<CommentDto> responseBody = webTestClient.get().uri("/api/v2/book/{bookId}/comment", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(responseBody)
                .isNotEmpty()
                .hasSize(2)
                .doesNotContain(modelToDtoMapper.commentToCommentDto(commentToDelete));
    }
}
