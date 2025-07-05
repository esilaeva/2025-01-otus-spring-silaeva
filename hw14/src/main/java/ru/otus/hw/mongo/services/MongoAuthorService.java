package ru.otus.hw.mongo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.mongo.model.MongoAuthor;
import ru.otus.hw.mongo.repositories.MongoAuthorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoAuthorService {

    private final MongoAuthorRepository mongoAuthorRepository;
    
    public List<MongoAuthor> getAuthors() {
        return mongoAuthorRepository.findAll();
    }

    public void clearAuthors() {
        mongoAuthorRepository.deleteAll();
    }
}
