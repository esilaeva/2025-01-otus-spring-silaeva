package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private  final GenreRepository genreRepository;

    private final EntityToDtoMapper mapper;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(mapper::genreToGenreDto)
                .toList();
    }
}
