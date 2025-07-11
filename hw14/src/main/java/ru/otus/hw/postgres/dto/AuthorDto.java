package ru.otus.hw.postgres.dto;

import java.io.Serializable;

/**
 * DTO for {@link ru.otus.hw.postgres.model.Author}
 */
public record AuthorDto(long id, String fullName) implements Serializable {
}