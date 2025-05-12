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
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);

        Author author = new Author();
        author.setFullName(authorFullName);
        book.setAuthor(author);

        Genre genre = new Genre();
        genre.setName(genreName);
        book.setGenre(genre);
        return book;
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
