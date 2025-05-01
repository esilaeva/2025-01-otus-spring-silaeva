package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(String id);

    List<BookDto> findAll();

    BookDto create(String title, String authorId, Set<String> genreId);

    BookDto update(String id, String title, String authorId, Set<String> genreId);

    void deleteById(String id);
}
