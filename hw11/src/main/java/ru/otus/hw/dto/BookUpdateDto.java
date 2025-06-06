package ru.otus.hw.dto;

import java.util.Set;

public record BookUpdateDto(String id,
                            String title,
                            String authorId,
                            Set<String> genresIds) {
}
