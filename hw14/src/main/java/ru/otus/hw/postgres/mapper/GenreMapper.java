package ru.otus.hw.postgres.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.otus.hw.postgres.dto.GenreDto;
import ru.otus.hw.postgres.model.Genre;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    GenreDto toDto(Genre genre);
}