package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;


    @ShellMethod(value = "Find comment by id", key = "ccid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse(NotFoundMessage.COMMENT.getMessage().formatted(id));
    }

    @ShellMethod(value = "Find all comments by book id", key = "cbid")
    public String findAllCommentsByBookId(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String bookId, String commentContent) {
        var savedComment = commentService.create(bookId, commentContent);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String newContent) {
        var savedComment = commentService.update(id, newContent);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }
}
