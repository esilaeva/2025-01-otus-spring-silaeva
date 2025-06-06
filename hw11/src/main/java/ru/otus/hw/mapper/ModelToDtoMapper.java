package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@Mapper(componentModel = "spring")
public interface ModelToDtoMapper {

    AuthorDto authorToAuthorDto(Author author);

    BookDto bookToBookDto(Book book);

    CommentDto commentToCommentDto(Comment comment);

    GenreDto genreToGenreDto(Genre genre);
}
