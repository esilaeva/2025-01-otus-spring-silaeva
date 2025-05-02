package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final EntityToDtoMapper mapper;


    @Override
    public Optional<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .map(mapper::bookToBookDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(mapper::bookToBookDto)
                .toList();
    }

    @Override
    public BookDto create(String title, String authorId, Set<String> genreIds) {
        boolean isInvalid = StringUtils.isBlank(title)
                || StringUtils.isBlank(authorId)
                || CollectionUtils.isEmpty(genreIds);
        validateInput(isInvalid,
                () -> ("Title and Author ID cannot be blank, and Genre IDs cannot be empty. " +
                        "Provided: title='%s', authorId='%s', genreIds=%s")
                        .formatted(title, authorId, genreIds));

        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), authorId);

        Set<Genre> genres = validateAndGetGenres(genreIds, genreRepository.findAllById(genreIds));

        var newBook = new Book();
        newBook.setTitle(title);
        newBook.setAuthor(author);
        newBook.setGenres(genres);
        Book savedBook = bookRepository.save(newBook);

        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genreIds) {
        boolean isInvalid = StringUtils.isBlank(id)
                || StringUtils.isBlank(title)
                || StringUtils.isBlank(authorId)
                || CollectionUtils.isEmpty(genreIds);
        validateInput(isInvalid, () -> ("Book ID, Title, and Author ID cannot be blank, " +
                "and Genre IDs cannot be empty. " +
                "Provided: id='%s', title='%s', authorId='%s', genreIds=%s").formatted(id, title, authorId, genreIds));

        var bookToUpdate = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), id);

        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), authorId);

        Set<Genre> genres = validateAndGetGenres(genreIds, genreRepository.findAllById(genreIds));

        bookToUpdate.setTitle(title);
        bookToUpdate.setAuthor(author);
        bookToUpdate.setGenres(genres);

        Book savedBook = bookRepository.save(bookToUpdate);

        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.BOOK.getMessage().formatted(id)));
        //First delete all comments related to the book being deleted
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    private static Set<Genre> validateAndGetGenres(Set<String> genreIds, List<Genre> foundGenresList) {
        if (foundGenresList.size() != genreIds.size()) {
            Set<String> foundGenreIds = foundGenresList.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<String> missingIds = new HashSet<>(genreIds);
            missingIds.removeAll(foundGenreIds);
            throw new EntityNotFoundException(NotFoundMessage.GENRE.getMessage().formatted(missingIds));
        }
        return new HashSet<>(foundGenresList);
    }

    private static <T> T checkAndGetEntity(Function<String, Optional<T>> repositoryMethod,
                                           String entityName,
                                           String id) {
        return repositoryMethod.apply(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        NotFoundMessage.ENTITY.getMessage().formatted(entityName, id)));
    }

    private static void validateInput(boolean isInvalid, Supplier<String> messageSupplier) {
        if (isInvalid) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
}
