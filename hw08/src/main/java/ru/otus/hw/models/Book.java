package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"genres", "author"})
public class Book {

    @Id
    private String id;

    private String title;

    private Author author;

    private Set<Genre> genres;

    public Book(String title, Author author, Set<Genre> genre) {
        this.title = title;
        this.author = author;
        this.genres = genre;
    }
}
