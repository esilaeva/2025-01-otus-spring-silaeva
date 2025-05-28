package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookCreateDto(

    @NotBlank(message = "The book title should not be empty.")
    String title,

    @NotNull(message = "The author id field is required.")
    Long authorId,

    @NotNull(message = "The genre id field is required.")
    Long genreId
) {}
