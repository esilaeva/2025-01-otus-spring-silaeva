package ru.otus.hw.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.postgres.model.Genre;

public interface PostgresGenreRepository extends JpaRepository<Genre, Long> {
}
