package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class BookRestController {

    private final BookService bookService;


    @GetMapping("/book")
    public ResponseEntity<List<BookDto>> findAll() {

        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable long id) {

        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping("/book")
    public ResponseEntity<BookDto> create(@RequestBody @Valid BookCreateDto bookCreateDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.create(bookCreateDto));
    }

    @PutMapping("/book")
    public ResponseEntity<BookDto> update(@RequestBody @Valid BookUpdateDto bookUpdateDto) {

        return ResponseEntity.ok(bookService.update(bookUpdateDto));
    }

    @DeleteMapping("/book/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {

        bookService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
