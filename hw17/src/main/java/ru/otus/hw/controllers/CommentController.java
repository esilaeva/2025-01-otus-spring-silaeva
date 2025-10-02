package ru.otus.hw.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.mapper.DtoToDtoMapper;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private static final String BOOK_ID = "bookId";

    private static final String ATTRIBUTE_COMMENT = "comment";

    private static final String REDIRECT_BOOKS_COMMENTS = "redirect:/book/%s/comments";

    private final CommentService commentService;

    private final DtoToDtoMapper dtoToDtoMapper;


    @GetMapping("/book/{bookId}/comments")
    public String allCommentsPage(@PathVariable long bookId, Model model) {
        List<CommentDto> comments = commentService.findByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute(BOOK_ID, bookId);

        return "comments";
    }

    @GetMapping("/comment/{id}")
    public String editCommentPage(@PathVariable long id, Model model) {
        CommentDto comment = commentService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id)));

        model.addAttribute(ATTRIBUTE_COMMENT, dtoToDtoMapper.commentDtoToCommentUpdateDto(comment));
        model.addAttribute(BOOK_ID, comment.bookId());

        return "comment-edit";
    }


    @PostMapping("/comment")
    public String addComment(@Validated @ModelAttribute(ATTRIBUTE_COMMENT) CommentCreateDto commentCreateDto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return REDIRECT_BOOKS_COMMENTS.formatted(commentCreateDto.bookId());
        }

        commentService.create(commentCreateDto);

        return REDIRECT_BOOKS_COMMENTS.formatted(commentCreateDto.bookId());
    }

    @PutMapping("/book/{bookId}/comment")
    public String updateComment(@PathVariable long bookId,
                                @Validated @ModelAttribute(ATTRIBUTE_COMMENT) CommentUpdateDto commentUpdateDto,
                                BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(BOOK_ID, bookId);

            return "comment-edit";
        }

        commentService.update(commentUpdateDto);

        return REDIRECT_BOOKS_COMMENTS.formatted(bookId);
    }

    @DeleteMapping("/book/{bookId}/comment/{id}")
    public String deleteComment(@PathVariable long bookId, @PathVariable long id) {
        commentService.deleteById(id);

        return REDIRECT_BOOKS_COMMENTS.formatted(bookId);
    }
}
