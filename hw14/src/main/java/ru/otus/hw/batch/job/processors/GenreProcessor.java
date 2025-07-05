package ru.otus.hw.batch.job.processors;

import org.bson.types.ObjectId;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.mongo.model.MongoGenre;
import ru.otus.hw.postgres.model.Genre;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@StepScope
public class GenreProcessor implements ItemProcessor<Genre, MongoGenre> {

    private ExecutionContext jobExecutionContext;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        if (!jobExecutionContext.containsKey("genreIdMap")) {
            jobExecutionContext.put("genreIdMap", new ConcurrentHashMap<Long, String>());
        }
    }

    @Override
    public MongoGenre process(Genre genreSqlItem) throws Exception {
        Map<Long, String> genreIdMap = (Map<Long, String>) jobExecutionContext.get("genreIdMap");

        MongoGenre mongoGenre = new MongoGenre();
        mongoGenre.setId(ObjectId.get().toHexString());
        mongoGenre.setName(genreSqlItem.getName());

        genreIdMap.put(genreSqlItem.getId(), mongoGenre.getId());

        return mongoGenre;
    }
}
