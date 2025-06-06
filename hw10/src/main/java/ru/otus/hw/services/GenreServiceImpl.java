package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final EntityToDtoMapper entityToDtoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {

        return genreRepository.findAll()
                .stream()
                .map(entityToDtoMapper::genreToGenreDto)
                .toList();
    }
}
