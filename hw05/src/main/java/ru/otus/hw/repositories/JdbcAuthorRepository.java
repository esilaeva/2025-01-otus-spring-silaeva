package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.getJdbcOperations()
                .query("SELECT ID, FULL_NAME FROM AUTHORS", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {

        Map<String, Object> params = Collections.singletonMap("id", id);

        return namedParameterJdbcOperations
                .query("SELECT ID, FULL_NAME FROM AUTHORS WHERE ID = :id", params, new AuthorRowMapper())
                .stream()
                .findFirst();
    }


    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return new Author(rs.getLong("id"), rs.getString("full_name"));
        }
    }
}