package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.utils.TestUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
@Import({BookServiceImpl.class, CommentServiceImpl.class})
@ComponentScan("ru.otus.hw.mapper")
@DisplayName("Service for working with books: ")
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModelToDtoMapper mapper;


    @Test
    @DisplayName("should return a book by id")
    void shouldReturnCorrectBookById() {
        Book expectedThirdBook = TestUtils.books.get(2);
        Mono<BookDto> actualThirdBook = bookService.findById(expectedThirdBook.getId());

        StepVerifier.create(actualThirdBook)
                .expectNext(mapper.bookToBookDto(expectedThirdBook))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return a list of all books")
    void shouldReturnCorrectBooksList() {
        var expectedAllBooks = TestUtils.books.stream()
                .map(mapper::bookToBookDto)
                .toList();
        Flux<BookDto> actualAllBooks = bookService.findAll();

        StepVerifier.create(actualAllBooks)
                .expectNextSequence(expectedAllBooks)
                .verifyComplete();
    }

    @Test
    @DisplayName("should create a new book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldCreateNewBook() {
        String newBookTitle = "New_Book_4";
        String newBookAuthorId = TestUtils.authors.get(1).getId();
        var newBookGenreIds = Set.of(TestUtils.genres.get(2).getId(), TestUtils.genres.get(3).getId());

        BookCreateDto bookCreateDto = new BookCreateDto(newBookTitle, newBookAuthorId, newBookGenreIds);

        var newBook = bookService.create(bookCreateDto);

        StepVerifier.create(newBook)
                .expectNextMatches(bookDto -> {
                    assertThat(bookDto.title()).isEqualTo(newBookTitle);
                    assertThat(bookDto.author().id()).isEqualTo(newBookAuthorId);
                    assertThat(bookDto.genres().stream()
                            .map(GenreDto::id)
                            .collect(Collectors.toSet()))
                            .isEqualTo(newBookGenreIds);
                    return true;
                }).verifyComplete();
    }

    @Test
    @DisplayName("should update a stored book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldUpdateStoredBook() {
        Book thirdBook = TestUtils.books.get(2);

        String newTitle = "New_Title_for_Book_3";
        String newAuthorId = TestUtils.authors.get(0).getId();
        var newGenreIds = Set.of(TestUtils.genres.get(0).getId(), TestUtils.genres.get(1).getId());

        BookUpdateDto bookUpdateDto = new BookUpdateDto(thirdBook.getId(), newTitle, newAuthorId, newGenreIds);

        Mono<BookDto> bookDtoMono = bookService.update(bookUpdateDto);

        StepVerifier.create(bookDtoMono)
                .expectNextMatches(bookDto -> {
                    assertThat(bookDto.title()).isEqualTo(newTitle);
                    assertThat(bookDto.author().id()).isEqualTo(newAuthorId);
                    assertThat(bookDto.genres().stream()
                            .map(GenreDto::id)
                            .collect(Collectors.toSet()))
                            .isEqualTo(newGenreIds);
                    return true;
                }).verifyComplete();
    }

    @Test
    @DisplayName("should delete a stored book and all related comments")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteBookAndRelatedCommentsById() {
        String bookIdToDelete = TestUtils.books.get(2).getId();

        StepVerifier.create(commentService.findByBookId(bookIdToDelete))
                .expectNextCount(3L)
                .verifyComplete();

        StepVerifier.create(bookService.deleteById(bookIdToDelete))
                .expectNextCount(0L)
                .verifyComplete();

        StepVerifier.create(bookService.findById(bookIdToDelete))
                .verifyError(EntityNotFoundException.class);

        StepVerifier.create(commentService.findByBookId(bookIdToDelete))
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("should throw an exception should be thrown when trying to find a book that doesn't exist")
    void throwExceptionToFindNonExistingBook() {
        Mono<BookDto> nonExistingBook = bookService.findById("non-existing-id");

        StepVerifier.create(nonExistingBook)
                .expectError(EntityNotFoundException.class)
                .verify();
    }
}