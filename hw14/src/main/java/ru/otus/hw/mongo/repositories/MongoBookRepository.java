package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.model.MongoBook;

public interface MongoBookRepository extends MongoRepository<MongoBook, String> {
}
