package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

@Mapper(componentModel = "spring")
public interface EntityToDtoMapper {

    AuthorDto authorToAuthorDto(Author author);

    BookDto bookToBookDto(Book book);

    @Mapping(target = "bookId", source = "book.id")
    CommentDto commentToCommentDto(Comment comment);

    GenreDto genreToGenreDto(Genre genre);
}