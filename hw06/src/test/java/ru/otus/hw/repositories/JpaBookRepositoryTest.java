package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.utils.TestUtils;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.repositories.JpaBookRepository.BOOK_NOT_FOUND;

@DisplayName("JPA based repository for working with books")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaBookRepository jpaBookRepository;


    private static Stream<Arguments> provideBooksIndexes() {
        return TestUtils.generateIndexesSequence(1, 3).map(Arguments::of);
    }

    @DisplayName("should load the book by id")
    @ParameterizedTest(name = "Book id is: {0}")
    @MethodSource("provideBooksIndexes")
    void shouldReturnCorrectBookById(long index) {
        var actualBook = jpaBookRepository.findById(index);
        var expectedBook = testEntityManager.find(Book.class, index);

        assertThat(actualBook)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
    }

    @DisplayName("should load a list of all books")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = jpaBookRepository.findAll();
        var expectedBooks = TestUtils.generateIndexesSequence(1, 3)
                .map(id -> testEntityManager.find(Book.class, id))
                .toList();

        assertThat(actualBooks).containsExactlyInAnyOrderElementsOf(expectedBooks);
    }

    @DisplayName("should save a new book")
    @Test
    void shouldSaveNewBook() {
        var author = testEntityManager.find(Author.class, 1L);
        var genre = testEntityManager.find(Genre.class, 1L);

        var expectedBook = new Book(0, "BookTitle_10500", author, genre);
        var returnedBook = jpaBookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(testEntityManager.find(Book.class, returnedBook.getId()))
                .isNotNull()
                .isEqualTo(returnedBook);
    }

    @DisplayName("should save a modified book")
    @Test
    void shouldSaveUpdatedBook() {
        var author = testEntityManager.find(Author.class, 2L);
        var genre = testEntityManager.find(Genre.class, 2L);

        var expectedBook = new Book(FIRST_BOOK_ID, "BookTitle_10500", author, genre);
        var returnedBook = jpaBookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(testEntityManager.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("should delete a book by id")
    @Test
    void shouldDeleteBook() {
        assertThat(jpaBookRepository.findById(FIRST_BOOK_ID)).isPresent();
        jpaBookRepository.deleteById(FIRST_BOOK_ID);
        assertThat(jpaBookRepository.findById(FIRST_BOOK_ID)).isEmpty();
    }


    private static Stream<Arguments> provideNonExistentBooksId() {
        return TestUtils.generateIndexesSequence(4, 6).map(Arguments::of);
    }

    @DisplayName("should throws exception to remove non-existent books")
    @MethodSource("provideNonExistentBooksId")
    @ParameterizedTest
    void shouldThrowExceptionForNonExistentBook(long bookId) {
        assertThatThrownBy(() -> jpaBookRepository.deleteById(bookId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(BOOK_NOT_FOUND.formatted(bookId));
    }
}