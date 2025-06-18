package ru.otus.hw.services;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.TestUtils.createExpectedBook;


@DataJpaTest
@Import(BookServiceImpl.class)
@ComponentScan("ru.otus.hw.mapper")
@Transactional(propagation = Propagation.NEVER)
class BookServiceTest {

    private static final long NON_EXIST_ID = 999L;

    private static final String[] FIELDS_TO_COMPARE = {"id", "title", "author.fullName", "genre.name"};

    @Autowired
    private BookService bookService;

    @Autowired
    private EntityToDtoMapper mapper;


    @Test
    void shouldReturnCorrectBookById() {
        var actualBook = bookService.findById(1L);
        var expectedBook = createExpectedBook(1L, "BookTitle_1",
                "Author_1", "Genre_1");

        assertThat(actualBook)
                .usingRecursiveComparison()
                .comparingOnlyFields(FIELDS_TO_COMPARE)
                .isEqualTo(mapper.bookToBookDto(expectedBook));
    }

    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        var expectedBooks = Stream.of(
                        createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"),
                        createExpectedBook(3L, "BookTitle_3", "Author_3", "Genre_3"))
                .map(mapper::bookToBookDto)
                .toList();

        assertThat(actualBooks)
                .isNotEmpty()
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withComparedFields(FIELDS_TO_COMPARE)
                                .build())
                .containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldCreateNewBook() {
        var newBook = bookService.create(
                new BookCreateDto("New_Book_Title", 1L, 3L)
        );
        var expectedBook = createExpectedBook(newBook.id(),
                "New_Book_Title", "Author_1", "Genre_3");
        var retrievedBook = bookService.findById(newBook.id());

        assertThat(retrievedBook)
                .usingRecursiveComparison()
                .comparingOnlyFields(FIELDS_TO_COMPARE)
                .isEqualTo(expectedBook);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveUpdatedBook() {
        bookService.update(
                new BookUpdateDto(1L, "Updated_Book_Title", 1L, 1L)
        );
        var actualBook = bookService.findById(1L);
        var expectedBook = createExpectedBook(1L, "Updated_Book_Title",
                "Author_1", "Genre_1");

        assertThat(actualBook)
                .usingRecursiveComparison()
                .comparingOnlyFields(FIELDS_TO_COMPARE)
                .isEqualTo(mapper.bookToBookDto(expectedBook));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBookById() {
        bookService.deleteById(3L);

        var actualBooks = bookService.findAll();
        var expectedBooks = Stream.of(
                        createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"))
                .map(mapper::bookToBookDto)
                .toList();

        assertThat(actualBooks)
                .isNotEmpty()
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator(
                        RecursiveComparisonConfiguration.builder()
                                .withComparedFields(FIELDS_TO_COMPARE)
                                .build())
                .containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @Test
    void shouldThrowsForNotExistingAuthor() {
        BookCreateDto bookCreateDto = new BookCreateDto("Book_Title", NON_EXIST_ID, 1L);
        assertThatThrownBy(() -> bookService.create(bookCreateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(NotFoundMessage.AUTHOR.getMessage().formatted(NON_EXIST_ID));
    }

    @Test
    void shouldThrowsForNotExistingGenre() {
        BookCreateDto bookCreateDto = new BookCreateDto("Book_Title", 1L, NON_EXIST_ID);
        assertThatThrownBy(() -> bookService.create(bookCreateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(NotFoundMessage.GENRE.getMessage().formatted(NON_EXIST_ID));
    }

    @Test
    void shouldThrowsForNotExistingBook() {
        BookUpdateDto bookUpdateDto = new BookUpdateDto(NON_EXIST_ID, "Book_Title", 1L, 1L);
        assertThatThrownBy(() -> bookService.update(bookUpdateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(NotFoundMessage.BOOK.getMessage().formatted(NON_EXIST_ID));
    }
}
