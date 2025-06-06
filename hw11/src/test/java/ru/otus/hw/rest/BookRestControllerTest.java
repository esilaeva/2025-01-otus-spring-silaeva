package ru.otus.hw.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.utils.TestUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Book rest controller should: ")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ModelToDtoMapper modelToDtoMapper;


    @Test
    @DisplayName("return a list of all books")
    void getAllBooks() {

        List<BookDto> bookDtoList = TestUtils.books.stream()
                .map(modelToDtoMapper::bookToBookDto)
                .toList();

        webTestClient.get().uri("/api/v2/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(bookDtoList.size())
                .value(actualBookDtos -> assertThat(actualBookDtos)
                        .containsExactlyInAnyOrderElementsOf(bookDtoList));
    }

    @Test
    @DisplayName("return a book by id")
    void getBookByBookId() {

        Book firstBook = TestUtils.books.get(0);

        webTestClient.get().uri("/api/v2/book/{id}", firstBook.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .isEqualTo(modelToDtoMapper.bookToBookDto(firstBook));
    }

    @Test
    @DisplayName("insert a new book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void insertNewBook() {

        String newBookTitle = "New_Book_4";
        Author authorForNewBook = TestUtils.authors.get(0);
        List<Genre> genresForNewBookModels = TestUtils.genres.stream().limit(2).toList();
        Set<String> genreIdsForNewBook = genresForNewBookModels.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        BookCreateDto bookCreateDto = new BookCreateDto(
                newBookTitle,
                authorForNewBook.getId(),
                genreIdsForNewBook
        );

        webTestClient.post().uri("/api/v2/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookCreateDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(bookDto -> {
                    assertThat(bookDto.id()).isNotNull().isNotBlank();
                    assertThat(bookDto)
                            .extracting(
                                    BookDto::title,
                                    dto -> dto.author().fullName(),
                                    dto -> dto.genres().stream().map(GenreDto::id).collect(Collectors.toSet())
                            )
                            .containsExactly(
                                    newBookTitle,
                                    authorForNewBook.getFullName(),
                                    genreIdsForNewBook
                            );
                    assertThat(bookDto.author().id()).isEqualTo(authorForNewBook.getId());
                });
    }

    @Test
    @DisplayName("update an existing book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateExistingBook() {

        Book bookForUpdate = TestUtils.books.get(0);
        Author newAuthor = TestUtils.authors.get(1);
        List<Genre> newGenres = TestUtils.genres.stream().limit(2).toList();
        Set<String> newGenreIds = newGenres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        BookUpdateDto bookUpdateDto = new BookUpdateDto(bookForUpdate.getId(),
                "New_Title",
                newAuthor.getId(),
                newGenreIds);

        webTestClient.put().uri("/api/v2/book")
                .contentType(MediaType.APPLICATION_JSON)  // Important for PUT requests
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookUpdateDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(updatedBook -> {

                    assertThat(updatedBook.id())
                            .isNotNull()
                            .isNotBlank()
                            .isEqualTo(bookForUpdate.getId());

                    assertThat(updatedBook)
                            .extracting(
                                    BookDto::title,
                                    bookDto -> bookDto.author().fullName(),
                                    bookDto -> bookDto.genres().stream()
                                            .map(GenreDto::id)
                                            .collect(Collectors.toSet())
                            )
                            .containsExactly(
                                    "New_Title",
                                    newAuthor.getFullName(),
                                    newGenreIds
                            );

                    assertThat(updatedBook.author().id())
                            .isEqualTo(newAuthor.getId());
                });
    }

    @Test
    @DisplayName("delete an existing book")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteBookByBookId() {

        Book bookToDelete = TestUtils.books.get(1);

        webTestClient.delete().uri("/api/v2/book/{bookId}", bookToDelete.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        List<BookDto> remainingBooks = webTestClient.get().uri("/api/v2/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(remainingBooks).isNotEmpty().doesNotContain(modelToDtoMapper.bookToBookDto(bookToDelete));
    }
}
