package ru.otus.hw.batch.job.processors;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.otus.hw.mongo.model.MongoAuthor;
import ru.otus.hw.mongo.model.MongoBook;
import ru.otus.hw.mongo.model.MongoGenre;
import ru.otus.hw.postgres.model.Book;
import ru.otus.hw.postgres.model.Genre;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@StepScope
public class BookProcessor implements ItemProcessor<Book, MongoBook> {

    private Map<Long, String> authorIdMap;

    private Map<Long, String> genreIdMap;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        // Retrieve the cache maps populated by previous steps
        ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        this.authorIdMap = (Map<Long, String>) jobExecutionContext.get("authorIdMap");
        this.genreIdMap = (Map<Long, String>) jobExecutionContext.get("genreIdMap");
    }

    @Override
    public MongoBook process(@NonNull Book bookSqlItem) throws Exception {
        MongoAuthor mongoAuthorRef = getAuthorReference(bookSqlItem);
        Set<MongoGenre> mongoGenreRefs = bookSqlItem.getGenres().stream()
                .map(this::getGenreReference)
                .collect(Collectors.toSet());

        MongoBook mongoBook = new MongoBook();
        mongoBook.setTitle(bookSqlItem.getTitle());
        mongoBook.setMongoAuthor(mongoAuthorRef);
        mongoBook.setMongoGenres(mongoGenreRefs);
        return mongoBook;
    }

    private MongoGenre getGenreReference(Genre genre) {
        String genreMongoId = genreIdMap.get(genre.getId());
        if (genreMongoId == null) {
            throw new IllegalArgumentException(
                    "Genre not found in context for Genre from Postgres id: %s".formatted(genre.getId()));
        }
        MongoGenre mongoGenre = new MongoGenre();
        mongoGenre.setId(genreMongoId);
        mongoGenre.setName(genre.getName());
        return mongoGenre;
    }

    private MongoAuthor getAuthorReference(Book book) {
        String authorMongoId = authorIdMap.get(book.getAuthor().getId());
        if (authorMongoId == null) {
            throw new IllegalArgumentException(
                    "Author not found in context for Author from Postgres id: %s".formatted(book.getAuthor().getId()));
        }
        MongoAuthor mongoAuthor = new MongoAuthor();
        mongoAuthor.setId(authorMongoId);
        mongoAuthor.setFullName(book.getAuthor().getFullName());
        return mongoAuthor;
    }
}
