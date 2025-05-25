package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {

    @NotNull(message = "The book id field is required.")
    private Long bookId;

    @NotBlank(message = "The comment content should not be empty.")
    private String content;
}

