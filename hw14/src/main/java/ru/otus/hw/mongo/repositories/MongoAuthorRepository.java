package ru.otus.hw.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.mongo.model.MongoAuthor;

public interface MongoAuthorRepository extends MongoRepository<MongoAuthor, String> {

}
