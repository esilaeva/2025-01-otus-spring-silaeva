package ru.otus.hw.postgres.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.postgres.model.Book;

import java.util.List;

public interface PostgresBookRepository extends JpaRepository<Book, Long> {

    @NonNull
    @EntityGraph(attributePaths = "author")
    List<Book> findAll();
}
