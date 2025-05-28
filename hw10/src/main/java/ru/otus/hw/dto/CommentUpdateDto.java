package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateDto(

        @NotNull(message = "The comment id should not be empty.")
        Long id,

        @NotBlank(message = "The comment content should not be empty.")
        String content
) {
}


