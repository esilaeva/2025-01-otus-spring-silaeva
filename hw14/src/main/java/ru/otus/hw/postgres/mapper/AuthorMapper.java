package ru.otus.hw.postgres.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.postgres.dto.AuthorDto;
import ru.otus.hw.postgres.model.Author;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {

    AuthorDto toDto(Author author);
}