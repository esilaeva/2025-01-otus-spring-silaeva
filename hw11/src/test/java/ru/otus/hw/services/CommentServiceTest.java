package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
@Import({CommentServiceImpl.class})
@ComponentScan("ru.otus.hw.mapper")
@DisplayName("Service for working with comments: ")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModelToDtoMapper mapper;


    @DisplayName("should return a comment by id")
    @Test
    void shouldReturnCorrectCommentById() {
        String firstCommentId = TestUtils.comments.get(0).getId();
        var expectedComment = mapper.commentToCommentDto(TestUtils.comments.get(0));

        StepVerifier.create(commentService.findById(firstCommentId))
                .expectNext(expectedComment)
                .verifyComplete();
    }

    @DisplayName("should return a list of the comments for a given books")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var firstBook = TestUtils.books.get(0);
        List<CommentDto> expectedComments = TestUtils.comments.stream()
                .filter(comment -> comment.getBook().equals(firstBook))
                .map(mapper::commentToCommentDto)
                .toList();

        StepVerifier.create(commentService.findByBookId(firstBook.getId()))
                .expectNextSequence(expectedComments)
                .verifyComplete();
    }

    @DisplayName("should update an existing comment for a given books")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldUpdateExistingCommentForGivenBook() {
        Comment firstComment = TestUtils.comments.get(0);
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(firstComment.getId(),
                "New content for first comment");
        CommentDto commentDto = new CommentDto(firstComment.getId(), "New content for first comment");

        StepVerifier.create(commentService.update(commentUpdateDto))
                .expectNext(commentDto)
                .verifyComplete();
    }

    @DisplayName("should create a new comment for a given books")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldCreateNewCommentForGivenBook() {
        var firstBook = TestUtils.books.get(0);
        CommentCreateDto commentCreateDto = new CommentCreateDto(firstBook.getId(),
                "New comment for book 1");
        CommentDto expected = new CommentDto(null, "New comment for book 1");

        StepVerifier.create(commentService.create(commentCreateDto))
                .assertNext(commentDto -> {
                    assertThat(commentDto.content()).isEqualTo(expected.content());
                }).verifyComplete();
    }

    @DisplayName("should delete a comment by id")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void shouldDeleteCommentById() {
        Comment firstComment = TestUtils.comments.get(0);
        StepVerifier.create(commentService.deleteById(firstComment.getId()))
                .expectNextCount(0L)
                .verifyComplete();
    }
}