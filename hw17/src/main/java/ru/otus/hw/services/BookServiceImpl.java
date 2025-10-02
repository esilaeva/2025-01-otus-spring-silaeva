package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final EntityToDtoMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long bookId) {

        return mapper.bookToBookDto(
                checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {

        return bookRepository.findAll()
                .stream()
                .map(mapper::bookToBookDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto create(BookCreateDto bookCreateDto) {
        if (bookCreateDto == null) {
            throw new IllegalArgumentException("bookCreateDto is null");
        }
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), bookCreateDto.authorId());
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), bookCreateDto.genreId());

        return mapper.bookToBookDto(
                bookRepository.save(new Book(0, bookCreateDto.title(), author, genre))
        );
    }

    @Override
    @Transactional
    public BookDto update(BookUpdateDto bookUpdateDto) {
        if (bookUpdateDto == null) {
            throw new IllegalArgumentException("bookUpdateDto is null");
        }
        var book = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookUpdateDto.id());
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), bookUpdateDto.authorId());
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), bookUpdateDto.genreId());

        book.setTitle(bookUpdateDto.title());
        book.setAuthor(author);
        book.setGenre(genre);

        return mapper.bookToBookDto(
                bookRepository.save(book)
        );
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }


    private static <T> T checkAndGetEntity(LongFunction<Optional<T>> repositoryMethod, String entityName, long id) {
        return repositoryMethod.apply(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.ENTITY.getMessage()
                        .formatted(entityName, id)));
    }
}