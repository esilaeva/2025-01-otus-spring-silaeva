package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private static final String BOOK_ID = "book_id";

    private static final String BOOK_TITLE = "book_title";

    private static final String AUTHOR_ID = "author_id";

    private static final String AUTHOR_NAME = "author_name";

    private static final String GENRE_ID = "genre_id";

    private static final String GENRE_NAME = "genre_name";

    private static final String PARAM_NAME_TITLE = "title";

    private static final String PARAM_NAME_AUTHOR_ID = "authorId";

    private static final String PARAM_NAME_GENRE_ID = "genreId";

    private static final String PARAM_NAME_ID = "id";

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {

        String sql = """
                SELECT 
                BOOKS.ID AS %s, 
                BOOKS.TITLE AS %s, 
                AUTHORS.ID AS %s, 
                AUTHORS.FULL_NAME AS %s, 
                GENRES.ID AS %s, 
                GENRES.NAME AS %s
                FROM BOOKS 
                JOIN AUTHORS ON BOOKS.AUTHOR_ID = AUTHORS.ID 
                JOIN GENRES ON BOOKS.GENRE_ID = GENRES.ID
                WHERE BOOKS.ID = :id""".formatted(BOOK_ID, BOOK_TITLE, AUTHOR_ID, AUTHOR_NAME, GENRE_ID, GENRE_NAME);
        var params = Collections.singletonMap("id", id);

        return namedParameterJdbcOperations.query(sql, params, new BookRowMapper())
                .stream()
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        String sql = """
                SELECT 
                BOOKS.ID AS %s, 
                BOOKS.TITLE AS %s, 
                AUTHORS.ID AS %s, 
                AUTHORS.FULL_NAME AS %s, 
                GENRES.ID AS %s, 
                GENRES.NAME AS %s
                FROM BOOKS 
                JOIN AUTHORS ON BOOKS.AUTHOR_ID = AUTHORS.ID 
                JOIN GENRES ON BOOKS.GENRE_ID = GENRES.ID
                """.formatted(BOOK_ID, BOOK_TITLE, AUTHOR_ID, AUTHOR_NAME, GENRE_ID, GENRE_NAME);

        return namedParameterJdbcOperations.getJdbcOperations().query(sql, new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var params = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update("DELETE FROM BOOKS WHERE BOOKS.ID = :id", params);
    }

    private Book insert(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PARAM_NAME_TITLE, book.getTitle());
        params.addValue(PARAM_NAME_AUTHOR_ID, book.getAuthor().getId());
        params.addValue(PARAM_NAME_GENRE_ID, book.getGenre().getId());

        var keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update("INSERT INTO BOOKS (TITLE, AUTHOR_ID, GENRE_ID) VALUES (:%s, :%s, :%s)"
                        .formatted(PARAM_NAME_TITLE, PARAM_NAME_AUTHOR_ID, PARAM_NAME_GENRE_ID), params, keyHolder);

        book.setId(Objects.requireNonNull(keyHolder.getKeyAs(Long.class)));
        return book;
    }

    private Book update(Book book) {
        var params = Map.of(PARAM_NAME_ID, book.getId(),
                PARAM_NAME_TITLE, book.getTitle(),
                PARAM_NAME_AUTHOR_ID, book.getAuthor().getId(),
                PARAM_NAME_GENRE_ID, book.getGenre().getId());

        int rowsAffected = namedParameterJdbcOperations.update(
                "UPDATE BOOKS SET TITLE = :%s, AUTHOR_ID = :%s, GENRE_ID = :%s WHERE BOOKS.ID = :%s"
                        .formatted(PARAM_NAME_TITLE, PARAM_NAME_AUTHOR_ID, PARAM_NAME_GENRE_ID, PARAM_NAME_ID), params);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(
                    "No rows affected. Book with id: %s not found".formatted(book.getId()));
        }
        return book;

    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var author = new Author(rs.getLong(AUTHOR_ID), rs.getString(AUTHOR_NAME));
            var genre = new Genre(rs.getLong(GENRE_ID), rs.getString(GENRE_NAME));
            return Book.builder()
                    .id(rs.getLong(BOOK_ID))
                    .title(rs.getString(BOOK_TITLE))
                    .author(author)
                    .genre(genre)
                    .build();
        }
    }
}
