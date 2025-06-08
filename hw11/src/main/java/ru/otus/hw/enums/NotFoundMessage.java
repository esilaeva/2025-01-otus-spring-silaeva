package ru.otus.hw.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotFoundMessage {
    ENTITY("%s with id: %s not found"),
    BOOK("Book with id: %s not found"),
    AUTHOR("Author with id: %s not found"),
    GENRE("Could not find all requested genres. Missing IDs: %s"),
    COMMENT("Comment with id: %s not found");

    private final String message;

}
