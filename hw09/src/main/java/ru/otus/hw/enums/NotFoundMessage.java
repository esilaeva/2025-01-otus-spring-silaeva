package ru.otus.hw.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotFoundMessage {
    ENTITY("%s with id: %d not found"),
    BOOK("Book with id: %d not found"),
    AUTHOR("Author with id: %d not found"),
    GENRE("Genre with id: %d not found"),
    COMMENT("Comment with id: %d not found");

    private final String message;

}
