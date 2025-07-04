package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final ModelToDtoMapper mapper;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
                .map(mapper::genreToGenreDto);
    }
}
