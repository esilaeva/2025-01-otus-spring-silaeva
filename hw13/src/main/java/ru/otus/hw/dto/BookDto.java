package ru.otus.hw.dto;


import lombok.Getter;

public record BookDto(@Getter long id, // need to Security acl engine to work properly
                      String title,
                      AuthorDto author,
                      GenreDto genre) {
}
