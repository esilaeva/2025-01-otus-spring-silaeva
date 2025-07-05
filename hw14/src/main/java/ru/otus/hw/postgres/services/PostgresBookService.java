package ru.otus.hw.postgres.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.postgres.dto.BookDto;
import ru.otus.hw.postgres.mapper.BookMapper;
import ru.otus.hw.postgres.repository.PostgresBookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostgresBookService {

    private final PostgresBookRepository bookRepository;

    private final BookMapper bookMapper;
    
    @Transactional(readOnly = true)
    public List<BookDto> getBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
