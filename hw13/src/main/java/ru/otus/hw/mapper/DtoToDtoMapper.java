package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;

@Mapper(componentModel = "spring")
public interface DtoToDtoMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "genreId", source = "genre.id")
    BookUpdateDto bookDtoToBookUpdateDto(BookDto bookDto);

    CommentUpdateDto commentDtoToCommentUpdateDto(CommentDto commentDto);

}
