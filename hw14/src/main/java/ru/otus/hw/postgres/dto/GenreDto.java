package ru.otus.hw.postgres.dto;

import java.io.Serializable;

/**
 * DTO for {@link ru.otus.hw.postgres.model.Genre}
 */
public record GenreDto(long id, String name) implements Serializable {
}