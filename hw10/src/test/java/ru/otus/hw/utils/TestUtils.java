package ru.otus.hw.utils;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TestUtils {

    public static Stream<Long> generateIndexesSequence(long from, long to) {
        return LongStream.rangeClosed(from, to).boxed();
    }

    public static Book createExpectedBook(long id, String title, String authorFullName, String genreName) {
        Book book = createBook(id, title);
        Author author = createAuthor(authorFullName);
        Genre genre = createGenre(genreName);

        book.setAuthor(author);
        book.setGenre(genre);
        return book;
    }

    public static Book createBook(long id, String title) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        return book;
    }

    public static Author createAuthor(String fullName) {
        Author author = new Author();
        author.setFullName(fullName);
        return author;
    }

    public static Genre createGenre(String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);
        return genre;
    }

    public static Comment createComment(long id, String commentContent) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(commentContent);
        return comment;
    }

    public static List<Long> getCommentsId(List<Comment> comments) {
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}
