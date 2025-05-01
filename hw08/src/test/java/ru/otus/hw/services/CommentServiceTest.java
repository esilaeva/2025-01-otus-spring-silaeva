package ru.otus.hw.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.mapper.EntityToDtoMapperImpl;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({CommentServiceImpl.class, EntityToDtoMapperImpl.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Service for working with Comments: ")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityToDtoMapper mapper;


    @Test
    @DisplayName("should return a comment by id")
    @Order(1)
    void shouldReturnCorrectCommentById() {
        String firstCommentId = TestUtils.comments.get(0).getId();
        var expectedComment = mapper.commentToCommentDto(TestUtils.comments.get(0));
        var actualComment = commentService.findById(firstCommentId);

        assertThat(actualComment)
                .isNotEmpty()
                .contains(expectedComment);
    }

    @Test
    @DisplayName("should return a list of the comments for a given books")
    @Order(2)
    void shouldReturnCorrectCommentsByBookId() {
        var firstBook = TestUtils.books.get(0);
        List<CommentDto> expectedComments = TestUtils.comments
                .stream()
                .filter(comment -> comment.getBook().equals(firstBook))
                .map(mapper::commentToCommentDto)
                .toList();

        List<CommentDto> actualComments = commentService.findByBookId(firstBook.getId());

        assertThat(actualComments)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    @DisplayName("should update an existing comment for a given books")
    @Order(3)
    void shouldUpdateExistingCommentForGivenBook() {
        var firstBook = TestUtils.books.get(0);
        var firstComment = commentService.findByBookId(firstBook.getId()).get(0);
        var updatedFirstComment = commentService.update(firstComment.id(), "Updated_Comment_1_for_Book_1");

        var actualFirstBookComments = commentService.findByBookId(firstBook.getId());

        assertThat(actualFirstBookComments)
                .isNotEmpty()
                .hasSize(3)
                .doesNotContain(firstComment)
                .contains(updatedFirstComment);
    }

    @Test
    @DisplayName("should create a new comment for a given books")
    @Order(4)
    void shouldCreateNewCommentForGivenBook() {
        var firstBook = TestUtils.books.get(0);
        var newCommentForFirstBook = commentService.create(firstBook.getId(), "New_comment_for_Book_1");
        assertThat(newCommentForFirstBook.id()).isNotNull();

        var actualComments = commentService.findByBookId(firstBook.getId());

        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(4)
                .contains(newCommentForFirstBook);
    }

    @Test
    @DisplayName("should delete a comment by id")
    @Order(5)
    void shouldDeleteCommentById() {
        var secondBook = TestUtils.books.get(1);
        var firstCommentForSecondBook = commentService.findByBookId(secondBook.getId()).get(0);
        assertThat(firstCommentForSecondBook).isNotNull();

        commentService.deleteById(firstCommentForSecondBook.id());

        var actualSecondBookComments = commentService.findByBookId(secondBook.getId());

        assertThat(actualSecondBookComments)
                .isNotEmpty()
                .hasSize(2)
                .doesNotContain(firstCommentForSecondBook);

    }
}