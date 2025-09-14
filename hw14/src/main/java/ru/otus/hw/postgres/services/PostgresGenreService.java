package ru.otus.hw.postgres.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.postgres.dto.GenreDto;
import ru.otus.hw.postgres.mapper.GenreMapper;
import ru.otus.hw.postgres.repositories.PostgresGenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostgresGenreService {

    private final PostgresGenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    public List<GenreDto> getGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toDto)
                .toList();
    }
}
