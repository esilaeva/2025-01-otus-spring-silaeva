package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final EntityToDtoMapper mapper;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(mapper::authorToAuthorDto)
                .toList();
    }
}
