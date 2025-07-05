package ru.otus.hw.postgres.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.postgres.dto.BookDto;
import ru.otus.hw.postgres.model.Book;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookDto toDto(Book book);
}