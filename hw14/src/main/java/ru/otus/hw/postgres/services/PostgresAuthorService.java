package ru.otus.hw.postgres.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.postgres.dto.AuthorDto;
import ru.otus.hw.postgres.mapper.AuthorMapper;
import ru.otus.hw.postgres.repositories.PostgresAuthorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostgresAuthorService {

    private final PostgresAuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Transactional(readOnly = true)
    public List<AuthorDto> getAuthors() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();

    }
}
