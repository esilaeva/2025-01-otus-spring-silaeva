package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private static final String REDIRECT_TO_COMMENTS_LIST = "redirect:/book/{bookId}/comments";

    private static final String BOOK_ID = "bookId";

    private final CommentService service;


    @GetMapping("/book/{bookId}/comments")
    public String allCommentsForBook(@PathVariable long bookId, Model model) {
        List<CommentDto> comments = service.findByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute(BOOK_ID, bookId);

        return "comments";
    }


    @PostMapping("/book/{bookId}/comment/add")
    public String addComment(@PathVariable long bookId, String context, Model model) {
        service.create(bookId, context);
        model.addAttribute(BOOK_ID, bookId);

        return REDIRECT_TO_COMMENTS_LIST;
    }

    @DeleteMapping("/book/{bookId}/comment/{id}")
    public String deleteComment(@PathVariable long bookId, @PathVariable long id, Model model) {
        service.deleteById(id);
        model.addAttribute(BOOK_ID, bookId);

        return REDIRECT_TO_COMMENTS_LIST;
    }

    @PatchMapping("/book/{bookId}/comment/{id}")
    public String showEditPage(@PathVariable long bookId, @PathVariable long id, Model model) {
        CommentDto comment = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found!"));
        model.addAttribute("comment", comment);
        model.addAttribute(BOOK_ID, bookId);

        return "comment-edit";
    }

    @PostMapping("/book/{bookId}/comment/{id}")
    public String updateComment(@PathVariable long bookId, @PathVariable long id, String content, Model model) {
        service.update(id, content);
        model.addAttribute(BOOK_ID, bookId);

        return REDIRECT_TO_COMMENTS_LIST;
    }

}
