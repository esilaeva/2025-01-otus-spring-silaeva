package ru.otus.hw.mongo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.mongo.model.MongoGenre;
import ru.otus.hw.mongo.repositories.MongoGenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoGenreService {

    private final MongoGenreRepository mongoGenreRepository;

    public List<MongoGenre> getGenres() {
        return mongoGenreRepository.findAll();
    }

    public void clearGenres() {
        mongoGenreRepository.deleteAll();
    }
}
