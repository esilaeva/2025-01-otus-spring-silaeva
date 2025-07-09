package ru.otus.hw.batch.job.processors;

import org.bson.types.ObjectId;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import ru.otus.hw.mongo.model.MongoAuthor;
import ru.otus.hw.postgres.model.Author;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@StepScope
public class AuthorProcessor implements ItemProcessor<Author, MongoAuthor> {

    private static final String AUTHOR_ID_MAP = "authorIdMap";

    private ExecutionContext jobExecutionContext;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        // Get the execution context from the job, which is shared across all steps
        jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        // Initialize the map in the context if it's the first time
        if (!jobExecutionContext.containsKey(AUTHOR_ID_MAP)) {
            jobExecutionContext.put(AUTHOR_ID_MAP, new ConcurrentHashMap<Long, String>());
        }
    }

    @Override
    public MongoAuthor process(Author authorSqlItem) throws Exception {
        // Retrieve the map from the context for processing chunk
        Map<Long, String> authorIdMap = (Map<Long, String>) jobExecutionContext.get(AUTHOR_ID_MAP);

        MongoAuthor mongoAuthor = new MongoAuthor();
        mongoAuthor.setId(ObjectId.get().toHexString());
        mongoAuthor.setFullName(authorSqlItem.getFullName());
        // Cache the ID mapping in the persistent JobExecutionContext
        authorIdMap.put(authorSqlItem.getId(), mongoAuthor.getId());
        return mongoAuthor;
    }
}
