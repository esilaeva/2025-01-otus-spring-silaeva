package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.HashSet;
import java.util.List;
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

    private final ModelToDtoMapper mapper;


    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(NotFoundMessage.BOOK.getMessage().formatted(id))))
                .map(mapper::bookToBookDto);
    }

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .map(mapper::bookToBookDto);
    }

    @Override
    public Mono<BookDto> create(BookCreateDto bookCreateDto) {
        boolean isInvalid = StringUtils.isBlank(bookCreateDto.title())
                || StringUtils.isBlank(bookCreateDto.authorId())
                || CollectionUtils.isEmpty(bookCreateDto.genresIds());
        validateInput(isInvalid,
                () -> ("Title, Author ID and Genre IDs cannot be blank. " +
                        "Provided: title='%s', authorId='%s', genreIds=%s")
                        .formatted(bookCreateDto.title(), bookCreateDto.authorId(), bookCreateDto.genresIds()));

        return fetchAndValidateAuthorAndGenres(bookCreateDto.authorId(),
                bookCreateDto.genresIds(),
                authorRepository,
                genreRepository)
                .flatMap(tuple -> {
                    var newBook = new Book();
                    newBook.setTitle(bookCreateDto.title());
                    newBook.setAuthor(tuple.getT1());
                    newBook.setGenres(new HashSet<>(tuple.getT2()));
                    return bookRepository.save(newBook);
                })
                .mapNotNull(mapper::bookToBookDto);
    }

    @Override
    public Mono<BookDto> update(BookUpdateDto bookUpdateDto) {
        boolean isInvalid = StringUtils.isBlank(bookUpdateDto.id()) || StringUtils.isBlank(bookUpdateDto.title())
                || StringUtils.isBlank(bookUpdateDto.authorId()) || CollectionUtils.isEmpty(bookUpdateDto.genresIds());
        validateInput(isInvalid, () -> ("Book ID, Title, and Author ID cannot be blank, " +
                "and Genre IDs cannot be empty. " +
                "Provided: id='%s', title='%s', authorId='%s', genreIds=%s")
                .formatted(bookUpdateDto.id(),
                        bookUpdateDto.title(),
                        bookUpdateDto.authorId(),
                        bookUpdateDto.genresIds()));
        var authorAndGenresMono = fetchAndValidateAuthorAndGenres(bookUpdateDto.authorId(), bookUpdateDto.genresIds(),
                authorRepository,
                genreRepository);
        return fetchAndValidateEntity(bookRepository::findById, Entity.BOOK.getName(), bookUpdateDto.id())
                .flatMap(bookToUpdate -> authorAndGenresMono.flatMap(tuple -> {
                    Author author = tuple.getT1();
                    List<Genre> genres = tuple.getT2();
                    bookToUpdate.setTitle(bookUpdateDto.title());
                    bookToUpdate.setAuthor(author);
                    bookToUpdate.setGenres(new HashSet<>(genres));
                    return bookRepository.save(bookToUpdate);
                })).mapNotNull(mapper::bookToBookDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        //First delete all comments related to the book being deleted
        return commentRepository.deleteByBookId(id)
                .then(bookRepository.deleteById(id));
    }


    private static <T> Mono<T> fetchAndValidateEntity(Function<String, Mono<T>> repositoryMethod,
                                                      String entityName,
                                                      String id) {
        return repositoryMethod.apply(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        NotFoundMessage.ENTITY.getMessage().formatted(entityName, id))));
    }

    private static <T> Flux<T> fetchAndValidateEntity(Function<Iterable<String>, Flux<T>> repositoryMethod,
                                                      String entityName,
                                                      Set<String> ids) {
        return repositoryMethod.apply(ids)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        NotFoundMessage.ENTITY.getMessage().formatted(entityName, ids))));
    }

    private static void validateInput(boolean isInvalid, Supplier<String> messageSupplier) {
        if (isInvalid) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    private static Mono<Tuple2<Author, List<Genre>>> fetchAndValidateAuthorAndGenres(String authorId,
                                                                                     Set<String> genreIds,
                                                                                     AuthorRepository authorRepository,
                                                                                     GenreRepository genreRepository) {

        Mono<Author> authorMono = fetchAndValidateEntity(authorRepository::findById, Entity.AUTHOR.getName(), authorId);

        Mono<List<Genre>> genresListMono = fetchAndValidateEntity(genreRepository::findAllById,
                Entity.GENRE.getName(), genreIds)
                .collectList()
                .flatMap(fetchedGenres -> {
                    if (fetchedGenres.size() != genreIds.size()) {
                        Set<String> foundGenreIds = fetchedGenres.stream()
                                .map(Genre::getId)
                                .collect(Collectors.toSet());
                        Set<String> missingIds = new HashSet<>(genreIds);
                        missingIds.removeAll(foundGenreIds);
                        return Mono.error(new EntityNotFoundException(
                                NotFoundMessage.GENRE.getMessage().formatted(missingIds)));
                    }
                    return Mono.just(fetchedGenres);
                });

        return Mono.zip(authorMono, genresListMono);
    }
}
