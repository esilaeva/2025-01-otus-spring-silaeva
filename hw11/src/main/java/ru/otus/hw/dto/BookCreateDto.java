package ru.otus.hw.dto;

import java.util.Set;

public record BookCreateDto(String title,
                            String authorId,
                            Set<String> genresIds) {
}
