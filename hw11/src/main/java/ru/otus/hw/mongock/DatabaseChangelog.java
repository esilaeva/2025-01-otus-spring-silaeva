package ru.otus.hw.mongock;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// https://docs.mongock.io/v4/changelogs/index.html for version 4 used here, deprecated for last version (5)
@ChangeLog
public class DatabaseChangelog {

    private List<Author> authors;

    private List<Genre> genres;

    private List<Book> books;


    @ChangeSet(order = "001", id = "dropDb", author = "esilaeva", runAlways = true)
    public void dropDb(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "esilaeva", runAlways = true)
    public void insertAuthors(MongockTemplate mongockTemplate) {
        authors = List.of(
                new Author("Author_1"),
                new Author("Author_2"),
                new Author("Author_3")
        );
        mongockTemplate.insertAll(authors);
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "esilaeva", runAlways = true)
    public void insertGenres(MongockTemplate mongockTemplate) {
        genres = List.of(
                new Genre("Genre_1"),
                new Genre("Genre_2"),
                new Genre("Genre_3"),
                new Genre("Genre_4"),
                new Genre("Genre_5"),
                new Genre("Genre_6")
        );
        mongockTemplate.insertAll(genres);
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "esilaeva", runAlways = true)
    public void insertBooks(MongockTemplate mongockTemplate) {
        books = List.of(
                new Book("Book_1", authors.get(0), Set.of(genres.get(0), genres.get(1))),
                new Book("Book_2", authors.get(1), Set.of(genres.get(2), genres.get(3))),
                new Book("Book_3", authors.get(2), Set.of(genres.get(4), genres.get(5)))
        );
        mongockTemplate.insertAll(books);
    }

    @ChangeSet(order = "005", id = "insertComments", author = "esilaeva", runAlways = true)
    public void insertComments(MongockTemplate mongockTemplate) {
        List<Comment> comments = new ArrayList<>();
        books.forEach(book -> comments.addAll(
                List.of(new Comment("Comment_1_for_%s".formatted(book.getTitle()), book),
                        new Comment("Comment_2_for_%s".formatted(book.getTitle()), book),
                        new Comment("Comment_3_for_%s".formatted(book.getTitle()), book)))
        );
        mongockTemplate.insertAll(comments);
    }
}
