package ru.otus.hw.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Entity {
    BOOK("Book"),
    AUTHOR("Author"),
    GENRE("Genre"),
    COMMENT("Comment");

    private final String name;
}
