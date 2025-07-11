package ru.otus.hw.postgres.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link ru.otus.hw.postgres.model.Book}
 */
public record BookDto(long id, String title, AuthorDto author, List<GenreDto> genres) implements Serializable {
}