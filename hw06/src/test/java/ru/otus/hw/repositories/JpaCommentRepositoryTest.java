package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.TestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.repositories.JpaCommentRepository.COMMENT_NOT_FOUND_MESSAGE;

@DisplayName("JPA based repository for working with book comments")
@DataJpaTest
@Import(JpaCommentRepository.class)
class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;


    private static Stream<Arguments> provideIndexes() {
        return TestUtils.generateIndexesSequence(1, 6).map(Arguments::of);
    }

    @DisplayName("should load the comment by id")
    @ParameterizedTest(name = "Comment id is: {0}")
    @MethodSource("provideIndexes")
    void shouldReturnCorrectCommentById(long index) {
        var actualComment = jpaCommentRepository.findById(index);
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
        assertThat(jpaCommentRepository.findById(999L)).isEmpty();
    }


    private static Stream<Arguments> provideBookIds() {
        return TestUtils.generateIndexesSequence(1, 3).map(Arguments::of);
    }

    @DisplayName("should return comment by book id")
    @MethodSource("provideBookIds")
    @ParameterizedTest
    void shouldReturnCorrectCommentsByBookId(long bookId) {
        var actualComments = jpaCommentRepository.findByBookId(bookId);
        var expectedComments = getCommentsId(actualComments)
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
        var actualComment = jpaCommentRepository.save(expectedComment);

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
        var actualComment = jpaCommentRepository.save(newComment);

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
        jpaCommentRepository.deleteById(commentId);
        assertThat(testEntityManager.find(Comment.class, commentId)).isNull();
    }

    private static Stream<Arguments> provideNonExistentCommentId() {
        return TestUtils.generateIndexesSequence(7, 10).map(Arguments::of);
    }

    @DisplayName("should throws exception to remove non-existent comments")
    @MethodSource("provideNonExistentCommentId")
    @ParameterizedTest
    void shouldThrowExceptionForNonExistentComment(long commentId) {
        assertThatThrownBy(() -> jpaCommentRepository.deleteById(commentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(COMMENT_NOT_FOUND_MESSAGE.formatted(commentId));
    }

    private List<Long> getCommentsId(List<Comment> comments) {
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}