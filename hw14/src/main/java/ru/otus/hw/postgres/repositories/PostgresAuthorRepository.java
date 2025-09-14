package ru.otus.hw.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.postgres.model.Author;

public interface PostgresAuthorRepository extends JpaRepository<Author, Long> {
}
