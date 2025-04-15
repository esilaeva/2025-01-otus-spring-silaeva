package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById(1L);
        assertThat(actualComment)
                .isPresent()
                .get()
                .extracting(Comment::getContent)
                .isEqualTo("Comment_1_for_BookTitle_1");
    }

    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var actualComments = commentService.findByBookId(1L);
        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(3)
                .extracting(Comment::getId, Comment::getContent)
                .containsExactlyInAnyOrder(
                        tuple(1L, "Comment_1_for_BookTitle_1"),
                        tuple(2L, "Comment_2_for_BookTitle_1"),
                        tuple(3L, "Comment_3_for_BookTitle_1"));
    }

    @Test
    void shouldInsertNewCommentForBook() {
        long newCommentId = commentService.insert(1L, "New_Comment_for_BookTitle_1").getId();
        var actualComments = commentService.findByBookId(1L);
        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(4)
                .extracting(Comment::getId, Comment::getContent)
                .containsExactlyInAnyOrder(
                        tuple(1L, "Comment_1_for_BookTitle_1"),
                        tuple(2L, "Comment_2_for_BookTitle_1"),
                        tuple(3L, "Comment_3_for_BookTitle_1"),
                        tuple(newCommentId, "New_Comment_for_BookTitle_1"));
    }

    @Test
    void shouldUpdateComment() {
        commentService.update(1L, "Updated_Comment_1_for_BookTitle_1");
        var actualComment = commentService.findByBookId(1L);
        assertThat(actualComment)
                .isNotEmpty()
                .hasSize(3)
                .extracting(Comment::getId, Comment::getContent)
                .containsExactlyInAnyOrder(
                        tuple(1L, "Updated_Comment_1_for_BookTitle_1"),
                        tuple(2L, "Comment_2_for_BookTitle_1"),
                        tuple(3L, "Comment_3_for_BookTitle_1"));
    }

    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById(3L);
        var actualComments = commentService.findByBookId(1L);
        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Comment::getId, Comment::getContent)
                .containsExactlyInAnyOrder(
                        tuple(1L, "Comment_1_for_BookTitle_1"),
                        tuple(2L, "Comment_2_for_BookTitle_1"));
    }
}