package ru.otus.hw.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;


    @Test
    void shouldReturnCorrectBookById() {
        var actualBook = bookService.findById(1L);
        assertThat(actualBook)
                .isPresent()
                .get()
                .extracting(Book::getId, Book::getTitle,
                        book -> book.getAuthor().getFullName(),
                        book -> book.getGenre().getName())
                .containsExactly(1L, "BookTitle_1", "Author_1", "Genre_1");
    }

    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        assertThat(actualBooks)
                .isNotEmpty()
                .hasSize(3)
                .extracting(Book::getId, Book::getTitle,
                        book -> book.getAuthor().getFullName(),
                        book -> book.getGenre().getName())
                .containsExactlyInAnyOrder(
                        tuple(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        tuple(2L, "BookTitle_2", "Author_2", "Genre_2"),
                        tuple(3L, "BookTitle_3", "Author_3", "Genre_3"));
    }

    @Test
    void shouldInsertNewBook() {
        var newBookId = bookService.insert("New_Book_Title", 1L, 3L).getId();
        var newBook = bookService.findById(newBookId);
        assertThat(newBook)
                .isPresent()
                .get()
                .extracting(Book::getId, Book::getTitle,
                        book -> book.getAuthor().getFullName(),
                        book -> book.getGenre().getName())
                .containsExactly(newBookId, "New_Book_Title", "Author_1", "Genre_3");
    }

    @Test
    void shouldSaveUpdatedBook() {
        bookService.update(1L, "Updated_Book_Title", 1L, 1L);
        var actualBook = bookService.findById(1L);
        assertThat(actualBook)
                .isPresent()
                .get()
                .extracting(Book::getId, Book::getTitle,
                        book -> book.getAuthor().getFullName(),
                        book -> book.getGenre().getName())
                .containsExactly(1L, "Updated_Book_Title", "Author_1", "Genre_1");
    }

    @Test
    void shouldDeleteBookById() {
        bookService.deleteById(3L);
        var deletedBook = bookService.findById(3L);
        assertThat(deletedBook).isEmpty();

        var actualBooks = bookService.findAll();
        assertThat(actualBooks)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Book::getId, Book::getTitle,
                        book -> book.getAuthor().getFullName(),
                        book -> book.getGenre().getName())
                .containsExactlyInAnyOrder(
                        tuple(1L, "BookTitle_1", "Author_1", "Genre_1"),
                        tuple(2L, "BookTitle_2", "Author_2", "Genre_2"));
    }
}