package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.TestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA based repository for working with book comments")
@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CommentRepository commentRepository;


    private static Stream<Arguments> provideIndexes() {
        return TestUtils.generateIndexesSequence(1, 6).map(Arguments::of);
    }

    @DisplayName("should load the comment by id")
    @ParameterizedTest(name = "Comment id is: {0}")
    @MethodSource("provideIndexes")
    void shouldReturnCorrectCommentById(long index) {
        var actualComment = commentRepository.findById(index);
        var expectedComment = testEntityManager.find(Comment.class, index);

        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("should return empty when comment ID not found")
    void shouldReturnEmptyForInvalidId() {
        assertThat(commentRepository.findById(999L)).isEmpty();
    }


    private static Stream<Arguments> provideBookIds() {
        return TestUtils.generateIndexesSequence(1, 3).map(Arguments::of);
    }

    @DisplayName("should return comment by book id")
    @MethodSource("provideBookIds")
    @ParameterizedTest
    void shouldReturnCorrectCommentsByBookId(long bookId) {
        var actualComments = commentRepository.findByBookId(bookId);
        var expectedComments = TestUtils.getCommentsId(actualComments)
                .stream()
                .map(id -> testEntityManager.find(Comment.class, id))
                .toList();

        assertThat(actualComments).containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @DisplayName("should save a new comment for book")
    @MethodSource("provideBookIds")
    @ParameterizedTest
    void shouldSaveNewComment(long bookId) {
        var book = testEntityManager.find(Book.class, bookId);
        var expectedComment = new Comment(0, "New comment for book %d".formatted(bookId), book);
        var actualComment = commentRepository.save(expectedComment);

        assertThat(actualComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);
    }

    @DisplayName("should update a comment for book")
    @Test
    void shouldUpdateComment() {
        var newComment = new Comment(6, "New content for comment",
                testEntityManager.find(Book.class, 3L));
        var actualComment = commentRepository.save(newComment);

        assertThat(actualComment)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newComment);

        var updatedComment = testEntityManager.find(Comment.class, 6L);

        assertThat(actualComment)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedComment);
    }

    private static Stream<Arguments> provideCommentIds() {
        return TestUtils.generateIndexesSequence(1, 6).map(Arguments::of);
    }

    @DisplayName("should delete a comment for book")
    @MethodSource("provideCommentIds")
    @ParameterizedTest
    void shouldDeleteCommentById(long commentId) {

        var comment = testEntityManager.find(Comment.class, commentId);
        assertThat(comment).isNotNull();
        commentRepository.deleteById(commentId);
        assertThat(testEntityManager.find(Comment.class, commentId)).isNull();
    }
}