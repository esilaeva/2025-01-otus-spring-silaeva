package ru.otus.hw.utils;

import org.bson.types.ObjectId;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestUtils {

    public static List<Author> authors = List.of(
            new Author(new ObjectId().toString(), "Author_1"),
            new Author(new ObjectId().toString(), "Author_2"),
            new Author(new ObjectId().toString(), "Author_3")
    );

    public static List<Genre> genres = List.of(
            new Genre(new ObjectId().toString(), "Genre_1"),
            new Genre(new ObjectId().toString(), "Genre_2"),
            new Genre(new ObjectId().toString(), "Genre_3"),
            new Genre(new ObjectId().toString(), "Genre_4"),
            new Genre(new ObjectId().toString(), "Genre_5"),
            new Genre(new ObjectId().toString(), "Genre_6")
    );

    public static List<Book> books = List.of(
            new Book(new ObjectId().toString(), "Book_1", authors.get(0), Set.of(genres.get(0), genres.get(1))),
            new Book(new ObjectId().toString(), "Book_2", authors.get(1), Set.of(genres.get(2), genres.get(3))),
            new Book(new ObjectId().toString(), "Book_3", authors.get(2), Set.of(genres.get(4), genres.get(5)))
    );

    public static List<Comment> comments = new ArrayList<>();

    static {
        books.forEach(book -> comments.addAll(
                List.of(
                        new Comment(new ObjectId().toString(), "Comment_1_for_%s".formatted(book.getTitle()), book),
                        new Comment(new ObjectId().toString(), "Comment_2_for_%s".formatted(book.getTitle()), book),
                        new Comment(new ObjectId().toString(), "Comment_3_for_%s".formatted(book.getTitle()), book)
                )
        ));
    }
}