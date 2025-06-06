package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "book")
public class Comment {

    @Id
    private String id;

    private String content;

    @DBRef(lazy = true)
    private Book book;

    public Comment(String content, Book book) {
        this.content = content;
        this.book = book;
    }
}
