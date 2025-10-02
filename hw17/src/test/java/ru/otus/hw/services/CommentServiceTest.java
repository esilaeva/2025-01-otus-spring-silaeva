package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.mapper.EntityToDtoMapper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.utils.TestUtils.createComment;

@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CommentServiceTest {

    private static final String IGNORED_FIELDS = "bookId";

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityToDtoMapper mapper;


    @Test
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById(1L);

        var expectedComment = createComment(1L, "Comment_1_for_BookTitle_1");

        assertThat(actualComment)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields(IGNORED_FIELDS)
                .isEqualTo(mapper.commentToCommentDto(expectedComment));
    }

    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var actualComments = commentService.findByBookId(1L);

        var expectedComments = Stream.of(
                        createComment(1L, "Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"),
                        createComment(3L, "Comment_3_for_BookTitle_1"))
                .map(mapper::commentToCommentDto)
                .toList();

        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELDS)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }


    @Test
    void shouldCreateNewCommentForBook() {
        var commentCreateDto = new CommentCreateDto(1L, "New_Comment_for_BookTitle_1");
        var newComment = commentService.create(commentCreateDto);
        var newCommentId = newComment.id();
        var actualComments = commentService.findByBookId(1L);

        var expectedComments = Stream.of(
                        createComment(1L, "Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"),
                        createComment(3L, "Comment_3_for_BookTitle_1"),
                        createComment(newCommentId, "New_Comment_for_BookTitle_1"))
                .map(mapper::commentToCommentDto)
                .toList();

        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELDS)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    void shouldUpdateComment() {
        var commentUpdateDto = new CommentUpdateDto(1L, "Updated_Comment_1_for_BookTitle_1");
        commentService.update(commentUpdateDto);
        var actualComment = commentService.findByBookId(1L);

        var expectedComments = Stream.of(
                        createComment(1L, "Updated_Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"),
                        createComment(3L, "Comment_3_for_BookTitle_1"))
                .map(mapper::commentToCommentDto)
                .toList();

        assertThat(actualComment)
                .isNotEmpty()
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELDS)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById(3L);
        var actualComments = commentService.findByBookId(1L);

        var expectedComments = Stream.of(
                        createComment(1L, "Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"))
                .map(mapper::commentToCommentDto)
                .toList();

        assertThat(actualComments)
                .isNotEmpty()
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELDS)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
}