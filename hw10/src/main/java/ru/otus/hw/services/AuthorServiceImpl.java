package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final EntityToDtoMapper entityToDtoMapper;

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDto> findAll() {

        return authorRepository.findAll()
                .stream()
                .map(entityToDtoMapper::authorToAuthorDto)
                .toList();
    }
}
