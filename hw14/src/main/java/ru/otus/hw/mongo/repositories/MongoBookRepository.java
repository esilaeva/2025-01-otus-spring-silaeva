package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.model.MongoBook;

import java.util.Optional;

public interface MongoBookRepository extends MongoRepository<MongoBook, String> {
    Optional<MongoBook> findByTitle(String bookTitle);
}
