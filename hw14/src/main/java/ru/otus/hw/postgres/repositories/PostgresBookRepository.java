package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.postgres.model.Book;

import java.util.List;
import java.util.Optional;

public interface PostgresBookRepository extends JpaRepository<Book, Long> {

    @NonNull
    @EntityGraph(value = "author-entity-graph")
    List<Book> findAll();

    @NonNull
    @EntityGraph(value = "author-genre-entity-graph")
    Optional<Book> findById(long id);
}
