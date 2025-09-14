package ru.otus.hw.mongo.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.mongo.services.MongoAuthorService;
import ru.otus.hw.mongo.services.MongoBookService;
import ru.otus.hw.mongo.services.MongoGenreService;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ShellComponent(value = "Mongo Shell Commands")
@RequiredArgsConstructor
public class MongoShellCommands {

    private final MongoAuthorService authorService;

    private final MongoBookService bookService;

    private final MongoGenreService genreService;

    @ShellMethod(value = "Get current Mongo data", key = {"mongo check", "mcheck", "mc"})
    public String getBooks() {
        System.out.printf("We store this Authors in Mongo:%n%s%n%n", iterableToString(authorService.getAuthors()));
        System.out.printf("We store this Genres in Mongo:%n%s%n%n", iterableToString(genreService.getGenres()));

        return "We store this Books in Mongo:%n%s%n%n".formatted(iterableToString(bookService.getBooks()));
    }

    @ShellMethod(value = "Clear Mongo data", key = {"mongo clear", "mclear", "mcl"})
    public String clearMongo() {
        authorService.clearAuthors();
        genreService.clearGenres();
        bookService.clearBooks();

        return "Mongo Data cleared!";
    }

    private static String iterableToString(Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(Object::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
