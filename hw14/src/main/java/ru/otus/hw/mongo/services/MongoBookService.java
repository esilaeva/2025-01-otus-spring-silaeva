package ru.otus.hw.mongo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.mongo.model.MongoBook;
import ru.otus.hw.mongo.repositories.MongoBookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoBookService {

    private final MongoBookRepository mongoBookRepository;
    
    public List<MongoBook> getBooks() {
        return mongoBookRepository.findAll();
    }

    public void clearBooks() {
        mongoBookRepository.deleteAll();
    }

}
