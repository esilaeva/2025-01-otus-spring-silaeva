package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ru.otus.hw.security.services.AclServiceWrapperService;

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

    private final AclServiceWrapperService aclService;

    private final PermissionEvaluator permissionEvaluator;


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

        Authentication authPrincipal = SecurityContextHolder.getContext().getAuthentication();

        return bookRepository.findAll()
                .stream()
                .filter(book -> permissionEvaluator.hasPermission(authPrincipal, book, "READ"))
                .map(mapper::bookToBookDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto create(BookCreateDto bookCreateDto) {

        if (bookCreateDto == null) {
            throw new IllegalArgumentException("bookCreateDto is null");
        }
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), bookCreateDto.getAuthorId());
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), bookCreateDto.getGenreId());

        Book savedBook = bookRepository.save(
                new Book(0, bookCreateDto.getTitle(), author, genre)
        );
        aclService.createPermission(Book.class, savedBook.getId());

        return mapper.bookToBookDto(savedBook);
    }

    // docs.spring.io/spring-security/reference/6.0/servlet/authorization/expression-based.html#el-common-built-in
    @Override
    @Transactional
    @PreAuthorize("hasPermission(#bookUpdateDto.id, 'ru.otus.hw.models.Book', 'WRITE')")
    public BookDto update(BookUpdateDto bookUpdateDto) {

        if (bookUpdateDto == null) {
            throw new IllegalArgumentException("Input Dto is null");
        }
        var book = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookUpdateDto.getId());
        var author = checkAndGetEntity(authorRepository::findById, Entity.AUTHOR.getName(), bookUpdateDto.getAuthorId());
        var genre = checkAndGetEntity(genreRepository::findById, Entity.GENRE.getName(), bookUpdateDto.getGenreId());

        book.setTitle(bookUpdateDto.getTitle());
        book.setAuthor(author);
        book.setGenre(genre);

        return mapper.bookToBookDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Book', 'WRITE')")
    public void deleteById(long id) {
        // First, delete the permissions associated with the book
        aclService.deletePermission(Book.class, id);

        bookRepository.deleteById(id);
    }


    private static <T> T checkAndGetEntity(LongFunction<Optional<T>> repositoryMethod, String entityName, long id) {
        return repositoryMethod.apply(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(NotFoundMessage.ENTITY.getMessage().formatted(entityName, id)));
    }
}