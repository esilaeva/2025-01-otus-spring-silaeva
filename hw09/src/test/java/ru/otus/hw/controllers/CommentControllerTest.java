package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.otus.hw.utils.TestUtils.createComment;


@DisplayName("Comment controller ")
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private static final String FIRST_BOOK_COMMENTS = "/book/1/comments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityToDtoMapper mapper;

    @MockitoBean
    private CommentService service;


    @DisplayName("Should return all comments page for book")
    @Test
    void shouldReturnAllCommentsForBookPage() throws Exception {

        List<CommentDto> commentsDtoList = Stream.of(createComment(1L, "Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"),
                        createComment(3L, "Comment_3_for_BookTitle_1"))
                .map(mapper::commentToCommentDto)
                .toList();

        when(service.findByBookId(1L)).thenReturn(commentsDtoList);

        mockMvc.perform(get("/book/{id}/comments", 1L))
                .andExpect(view().name("comments"))
                .andExpect(model().attribute("comments", commentsDtoList))
                .andExpect(model().attribute("bookId", 1L));
    }

    @DisplayName("Should return all comments page for book after add a new comment")
    @Test
    void shouldReturnAllCommentsPageAfterAddComment() throws Exception {
        mockMvc.perform(post("/book/{id}/comment/add", 1L)
                        .param("context", "New_Comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FIRST_BOOK_COMMENTS));

        verify(service, times(1)).create(1L, "New_Comment");
    }

    @DisplayName("Should return all comments page for book after remove a comment")
    @Test
    void shouldReturnAllCommentsPageAfterDelComment() throws Exception {
        mockMvc.perform(delete("/book/{bookId}/comment/{commentId}", 1L, 2L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FIRST_BOOK_COMMENTS));

        verify(service, times(1)).deleteById(2L);
    }

    @DisplayName("Should return edit page")
    @Test
    void showEditPage() throws Exception {
        CommentDto commentDto = mapper.commentToCommentDto(createComment(1L, "Comment_1"));
        when(service.findById(1L)).thenReturn(Optional.of(commentDto));

        mockMvc.perform(patch("/book/{book_id}/comment/{comment_id}", 2L, 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("comment-edit"))
                .andExpect(model().attribute("comment", commentDto))
                .andExpect(model().attribute("bookId", 2L));
    }

    @DisplayName("Should redirect to all comments page after update comment")
    @Test
    void shouldReturnAllCommentsPageAfterUpdateComment() throws Exception {

        mockMvc.perform(post("/book/{book_id}/comment/{comment_id}", 2L, 1L)
                        .param("content", "New_Comment_Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book/2/comments"));
        verify(service, times(1)).update(1L, "New_Comment_Content");
    }
}
