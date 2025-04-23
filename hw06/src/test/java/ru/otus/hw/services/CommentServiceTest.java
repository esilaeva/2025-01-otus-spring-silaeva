package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.utils.TestUtils.createComment;

@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class CommentServiceTest {
    
    private static final String IGNORED_FIELD = "book";
    
    @Autowired
    private CommentService commentService;
    
    
    @Test
    void shouldReturnCorrectCommentById() {
        var actualComment = commentService.findById(1L);
        
        var expectedComment = createComment(1L, "Comment_1_for_BookTitle_1");
        
        assertThat(actualComment)
            .isPresent()
            .get()
            .usingRecursiveComparison()
            .ignoringFields(IGNORED_FIELD)
            .isEqualTo(expectedComment);
    }
    
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var actualComments = commentService.findByBookId(1L);
        
        var expectedComments = List.of(
            createComment(1L, "Comment_1_for_BookTitle_1"),
            createComment(2L, "Comment_2_for_BookTitle_1"),
            createComment(3L, "Comment_3_for_BookTitle_1"));
        
        assertThat(actualComments)
            .isNotEmpty()
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELD)
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
    
    
    @Test
    void shouldCreateNewCommentForBook() {
        var newComment = commentService.create(1L, "New_Comment_for_BookTitle_1");
        var newCommentId = newComment.getId();
        var actualComments = commentService.findByBookId(1L);
        
        var expectedComments = List.of(
            createComment(1L, "Comment_1_for_BookTitle_1"),
            createComment(2L, "Comment_2_for_BookTitle_1"),
            createComment(3L, "Comment_3_for_BookTitle_1"),
            createComment(newCommentId, "New_Comment_for_BookTitle_1"));
        
        assertThat(actualComments)
            .isNotEmpty()
            .hasSize(4)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELD)
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
    
    @Test
    void shouldUpdateComment() {
        commentService.update(1L, "Updated_Comment_1_for_BookTitle_1");
        var actualComment = commentService.findByBookId(1L);
        
        var expectedComments = List.of(
            createComment(1L, "Updated_Comment_1_for_BookTitle_1"),
            createComment(2L, "Comment_2_for_BookTitle_1"),
            createComment(3L, "Comment_3_for_BookTitle_1"));
        
        assertThat(actualComment)
            .isNotEmpty()
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELD)
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
    
    @Test
    void shouldDeleteCommentById() {
        commentService.deleteById(3L);
        var actualComments = commentService.findByBookId(1L);
        
        var expectedComments = List.of(
            createComment(1L, "Comment_1_for_BookTitle_1"),
            createComment(2L, "Comment_2_for_BookTitle_1"));
        
        assertThat(actualComments)
            .isNotEmpty()
            .hasSize(2)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields(IGNORED_FIELD)
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
}