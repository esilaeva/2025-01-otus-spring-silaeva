package ru.otus.hw.services;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.hw.utils.TestUtils.createExpectedBook;


@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class BookServiceTest {
    
    public static final long NON_EXIST_ID = 999L;
    
    private static final String[] FIELDS_TO_COMPARE = {"id", "title", "author.fullName", "genre.name"};
    
    @Autowired
    private BookService bookService;
    
    
    @Test
    void shouldReturnCorrectBookById() {
        var actualBook = bookService.findById(1L);
        var expectedBook = createExpectedBook(1L, "BookTitle_1",
            "Author_1", "Genre_1");
        
        assertThat(actualBook)
            .usingRecursiveComparison()
            .comparingOnlyFields(FIELDS_TO_COMPARE)
            .isEqualTo(expectedBook);
    }
    
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        var expectedBooks = List.of(
            createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
            createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"),
            createExpectedBook(3L, "BookTitle_3", "Author_3", "Genre_3"));
        
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
    void shouldCreateNewBook() {
        var newBook = bookService.create("New_Book_Title", 1L, 3L);
        var expectedBook = createExpectedBook(newBook.getId(),
            "New_Book_Title", "Author_1", "Genre_3");
        var retrievedBook = bookService.findById(newBook.getId());
        
        assertThat(retrievedBook)
            .usingRecursiveComparison()
            .comparingOnlyFields(FIELDS_TO_COMPARE)
            .isEqualTo(expectedBook);
    }
    
    @Test
    void shouldSaveUpdatedBook() {
        bookService.update(1L, "Updated_Book_Title", 1L, 1L);
        var actualBook = bookService.findById(1L);
        var expectedBook = createExpectedBook(1L, "Updated_Book_Title",
            "Author_1", "Genre_1");
        
        assertThat(actualBook)
            .usingRecursiveComparison()
            .comparingOnlyFields(FIELDS_TO_COMPARE)
            .isEqualTo(expectedBook);
    }
    
    @Test
    void shouldResolveNullTitle() {
        bookService.update(1L, null, 2L, 3L);
        var actualBook = bookService.findById(1L);
        var expectedBook = createExpectedBook(1L, "BookTitle_1",
            "Author_2", "Genre_3");
        
        assertThat(actualBook)
            .usingRecursiveComparison()
            .comparingOnlyFields(FIELDS_TO_COMPARE)
            .isEqualTo(expectedBook);
    }
    
    @Test
    void shouldDeleteBookById() {
        bookService.deleteById(3L);
        
        var actualBooks = bookService.findAll();
        var expectedBooks = List.of(
            createExpectedBook(1L, "BookTitle_1", "Author_1", "Genre_1"),
            createExpectedBook(2L, "BookTitle_2", "Author_2", "Genre_2"));
        
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
        assertThatThrownBy(() -> bookService.create("Book_Title", NON_EXIST_ID, 1L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(NotFoundMessage.AUTHOR.getMessage().formatted(NON_EXIST_ID));
    }
    
    @Test
    void shouldThrowsForNotExistingGenre() {
        assertThatThrownBy(() -> bookService.create("Book_Title", 1L, NON_EXIST_ID))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(NotFoundMessage.GENRE.getMessage().formatted(NON_EXIST_ID));
    }
    
    @Test
    void shouldThrowsForNotExistingBook() {
        assertThatThrownBy(() -> bookService.update(NON_EXIST_ID, "Book_Title", 1L, 1L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage(NotFoundMessage.BOOK.getMessage().formatted(NON_EXIST_ID));
    }
}