package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateDto {

    private long id;

    @NotBlank(message = "The book title should not be empty.")
    private String title;

    @NotNull(message = "The author id field is required.")
    private Long authorId;

    @NotNull(message = "The genre id field is required.")
    private Long genreId;

}
