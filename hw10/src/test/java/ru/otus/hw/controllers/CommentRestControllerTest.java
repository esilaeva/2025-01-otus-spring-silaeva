package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.*;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.utils.TestUtils;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CommentRestController ")
@ComponentScan("ru.otus.hw.mapper")
@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @MockitoBean
    private CommentService commentService;


    @Test
    @DisplayName("Should return comment by id")
    void commentById() throws Exception {
        var commentDto = new CommentDto(1L, "Comment_Content",
                new BookDto(1L, "Book_Title",
                        new AuthorDto(1L, "Author_Name"),
                        new GenreDto(1L, "Genre_Name")
                ));

        when(commentService.findById(1L)).thenReturn(Optional.of(commentDto));

        mockMvc.perform(get("/api/v1/comment/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("Should return comments list for a book")
    void commentsByBookId() throws Exception {

        var commentDtoList = TestUtils.generateIndexesSequence(1, 3)
                .map(i -> TestUtils.createComment(i, "Comment_Content_%s".formatted(i)))
                .map(entityToDtoMapper::commentToCommentDto)
                .toList();

        when(commentService.findByBookId(1L)).thenReturn(commentDtoList);

        mockMvc.perform(get("/api/v1/book/{id}/comment", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDtoList)));
    }

    @Test
    @DisplayName("Should create a new comment")
    void createComment() throws Exception {

        var commentDto = new CommentDto(7L, "New_Comment_content",
                new BookDto(1L, "Book_Title",
                        new AuthorDto(1L, "Author_1"),
                        new GenreDto(1L, "Genre_1")));

        var commentCreateDto = new CommentCreateDto(1L, "New_Comment_content");

        when(commentService.create(commentCreateDto)).thenReturn(commentDto);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("Should update a comment")
    void updateComment() throws Exception {
        var commentDto = new CommentDto(1L, "Updated_Comment_content",
                new BookDto(1L, "Book_Title",
                        new AuthorDto(1L, "Author_1"),
                        new GenreDto(1L, "Genre_1")));

        var commentUpdateDto = new CommentUpdateDto(1L, "Updated_Comment_content");

        when(commentService.update(commentUpdateDto)).thenReturn(commentDto);

        mockMvc.perform(put("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("Should delete a comment")
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteById(1L);
    }


    @Test
    @DisplayName("Should return 404 error if comment is not found")
    void returnNotFoundError() throws Exception {

        when(commentService.findById(1L)).thenThrow(new EntityNotFoundException(
                NotFoundMessage.COMMENT.getMessage().formatted(1L))
        );
        var errorDto = new ErrorDto(404, NotFoundMessage.COMMENT.getMessage().formatted(1L));

        mockMvc.perform(get("/api/v1/comment/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(errorDto)));
    }
}