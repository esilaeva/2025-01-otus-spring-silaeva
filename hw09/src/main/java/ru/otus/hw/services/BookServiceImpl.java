package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
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
    public BookDto create(String title, long authorId, long genreId) {
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), authorId);
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), genreId);

        return mapper.bookToBookDto(
                bookRepository.save(new Book(0, title, author, genre))
        );
    }

    @Override
    @Transactional
    public BookDto update(long bookId, String title, long authorId, long genreId) {
        var book = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookId);
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), authorId);
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), genreId);

        book.setTitle(title);
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