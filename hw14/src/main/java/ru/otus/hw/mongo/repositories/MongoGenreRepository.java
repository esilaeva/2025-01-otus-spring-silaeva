package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.model.MongoGenre;

public interface MongoGenreRepository extends MongoRepository<MongoGenre, String> {
}
