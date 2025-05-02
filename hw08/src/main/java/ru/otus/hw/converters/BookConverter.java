package ru.otus.hw.converters;

import  lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(BookDto book) {
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.id(),
                book.title(),
                authorConverter.authorToString(book.author()),
                collectGenresToString(book));
    }

    private String collectGenresToString(BookDto book) {
        return book.genres()
                .stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining(", "));
    }
}
