package ru.otus.hw.postgres.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.postgres.services.PostgresAuthorService;
import ru.otus.hw.postgres.services.PostgresBookService;
import ru.otus.hw.postgres.services.PostgresGenreService;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ShellComponent(value = "Postgres Shell Commands")
@RequiredArgsConstructor
public class PostgresShellCommands {

    private final PostgresBookService bookService;

    private final PostgresAuthorService authorService;

    private final PostgresGenreService genreService;

    @ShellMethod(value = "Get current Postgres data", key = {"postgres check", "pcheck", "pc"})
    public String getBooks() {
        System.out.printf("We store this Authors in SQL DB:%n%s%n%n", iterableToString(authorService.getAuthors()));
        System.out.printf("We store this Genres in SQL DB:%n%s%n%n", iterableToString(genreService.getGenres()));

        return "We store this books in SQL DB:%n%s%n%n".formatted(iterableToString(bookService.getBooks()));
    }

    private static String iterableToString(Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(Object::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
