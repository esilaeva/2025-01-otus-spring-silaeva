package ru.otus.hw.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @NonNull
    @EntityGraph(value = "book:author-genre-entity-graph")
    Optional<Book> findById(long id);

    @NonNull
    @EntityGraph(value = "book:author-genre-entity-graph")
    List<Book> findAll();
}
