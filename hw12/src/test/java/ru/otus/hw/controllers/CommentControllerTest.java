package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.mapper.DtoToDtoMapper;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
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
@WebMvcTest(controllers = CommentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CommentControllerTest {

    private static final String FIRST_BOOK_COMMENTS = "/book/1/comments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Autowired
    private DtoToDtoMapper dtoToDtoMapper;

    @MockitoBean
    private CommentService service;


    @DisplayName("Should return all comments page for book")
    @Test
    void shouldReturnAllCommentsForBookPage() throws Exception {

        List<CommentDto> commentsDtoList = Stream.of(createComment(1L, "Comment_1_for_BookTitle_1"),
                        createComment(2L, "Comment_2_for_BookTitle_1"),
                        createComment(3L, "Comment_3_for_BookTitle_1"))
                .map(entityToDtoMapper::commentToCommentDto)
                .toList();

        when(service.findByBookId(1L)).thenReturn(commentsDtoList);

        mockMvc.perform(get("/book/{bookId}/comments", 1L))
                .andExpect(view().name("comments"))
                .andExpect(model().attribute("comments", commentsDtoList))
                .andExpect(model().attribute("bookId", 1L));
    }

    @DisplayName("Should return edit comment page")
    @Test
    void showCommentEditPage() throws Exception {
        CommentDto commentDto = entityToDtoMapper.commentToCommentDto(
                new Comment(1L, "Comment_1",
                        new Book(1L, "BookTitle_1",
                                new Author(1L, "AuthorName_1"),
                                new Genre(1L, "GenreName_1"))));

        when(service.findById(1L)).thenReturn(Optional.of(commentDto));

        mockMvc.perform(get("/comment/{comment_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("comment-edit"))
                .andExpect(model().attribute("comment", dtoToDtoMapper.commentDtoToCommentUpdateDto(commentDto)))
                .andExpect(model().attribute("bookId", 1L));
    }

    @DisplayName("Should redirect to the book comments page after add a new comment")
    @Test
    void shouldReturnAllCommentsPageAfterAddComment() throws Exception {

        CommentCreateDto commentCreateDto = new CommentCreateDto(1L, "New_Comment");
        mockMvc.perform(post("/comment")
                        .param("content", commentCreateDto.getContent())
                        .param("bookId", commentCreateDto.getBookId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FIRST_BOOK_COMMENTS));

        verify(service, times(1)).create(commentCreateDto);
    }

    @DisplayName("Should redirect to the book comments page after update a comment")
    @Test
    void shouldReturnAllCommentsPageAfterUpdateComment() throws Exception {

        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(1L, "New_Comment_Content");
        mockMvc.perform(put("/book/{book_id}/comment", 1L)
                        .param("id", Long.toString(commentUpdateDto.getId()))
                        .param("content", commentUpdateDto.getContent()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FIRST_BOOK_COMMENTS));

        verify(service, times(1)).update(commentUpdateDto);
    }

    @DisplayName("Should return redirect to the book comments page after remove a comment")
    @Test
    void shouldReturnAllCommentsPageAfterDelComment() throws Exception {
        mockMvc.perform(delete("/book/{bookId}/comment/{commentId}", 1L, 2L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(FIRST_BOOK_COMMENTS));

        verify(service, times(1)).deleteById(2L);
    }
}
