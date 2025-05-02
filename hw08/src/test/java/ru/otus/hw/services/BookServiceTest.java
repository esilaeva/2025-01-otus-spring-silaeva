package ru.otus.hw.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.mapper.EntityToDtoMapperImpl;
import ru.otus.hw.models.Book;
import ru.otus.hw.utils.TestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({BookServiceImpl.class, CommentServiceImpl.class, EntityToDtoMapperImpl.class})
@DisplayName("Service for working with Comments: ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookServiceTest {


    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityToDtoMapper mapper;

    @DisplayName("should return a book by id")
    @Test
    @Order(1)
    void shouldReturnCorrectBookById() {
        Book expectedThirdBook = TestUtils.books.get(2);
        var actualThirdBook = bookService.findById(expectedThirdBook.getId());

        assertThat(actualThirdBook)
                .isNotEmpty()
                .contains(mapper.bookToBookDto(expectedThirdBook));
    }

    @DisplayName("should return a list of all books")
    @Test
    @Order(2)
    void shouldReturnCorrectBooksList() {
        Book expectedThirdBook = TestUtils.books.get(2);
        var allBooks = bookService.findAll();

        assertThat(allBooks)
                .isNotEmpty()
                .hasSize(3)
                .contains(mapper.bookToBookDto(expectedThirdBook));

    }

    @DisplayName("should create a new book")
    @Test
    @Order(3)
    void shouldCreateNewBook() {
        String newBookTitle = "New_Book_4";
        String newBookAuthorId = TestUtils.authors.get(1).getId();
        var newBookGenreIds = Set.of(TestUtils.genres.get(2).getId(), TestUtils.genres.get(3).getId());

        var newBook = bookService.create(newBookTitle, newBookAuthorId, newBookGenreIds);

        assertThat(newBook).isNotNull();

        assertThat(bookService.findAll())
                .isNotEmpty()
                .hasSize(4)
                .contains(newBook);
    }

    @DisplayName("should update a stored book")
    @Test
    @Order(4)
    void shouldUpdateStoredBook() {
        Book thirdBook = TestUtils.books.get(2);

        String newTitle = "New_Title_for_Book_3";
        String newAuthorId = TestUtils.authors.get(0).getId();
        var newGenreIds = Set.of(TestUtils.genres.get(0).getId(), TestUtils.genres.get(1).getId());

        BookDto updatedBook = bookService.update(thirdBook.getId(), newTitle, newAuthorId, newGenreIds);

        assertThat(updatedBook).isNotNull();

        assertThat(bookService.findById(updatedBook.id()))
                .isNotEmpty()
                .contains(updatedBook);
    }

    @DisplayName("should delete a stored book with all related comments")
    @Test
    @Order(5)
    void deleteBookAndRelatedCommentsById() {
        String thirdBookId = TestUtils.books.get(2).getId();
        var thirdBookComments = commentService.findByBookId(thirdBookId);

        assertThat(thirdBookComments)
                .isNotEmpty()
                .hasSize(3);

        bookService.deleteById(thirdBookId);

        assertThat(bookService.findById(thirdBookId)).isEmpty();
        assertThat(commentService.findByBookId(thirdBookId)).isEmpty();

        thirdBookComments.forEach(comment -> assertThat(commentService.findById(comment.id())).isEmpty());
    }
}