package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentDto> findById(@PathVariable long id) {

        return ResponseEntity.ok(commentService.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id))));
    }

    @GetMapping("/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> findByBookId(@PathVariable long bookId) {

        return ResponseEntity.ok(commentService.findByBookId(bookId));
    }

    @PostMapping("/comment")
    public ResponseEntity<CommentDto> create(@Validated @RequestBody CommentCreateDto commentCreateDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commentService.create(commentCreateDto));
    }

    @PutMapping("/comment")
    public ResponseEntity<CommentDto> update(@Validated @RequestBody CommentUpdateDto commentUpdateDto) {

        return ResponseEntity.ok(commentService.update(commentUpdateDto));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        commentService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
