package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateDto(

        @NotNull(message = "The book id field is required.")
        Long bookId,

        @NotBlank(message = "The comment content should not be empty.")
        String content
) {
}
