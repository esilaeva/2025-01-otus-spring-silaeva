package ru.otus.hw.batch.job.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.mongo.model.MongoAuthor;
import ru.otus.hw.mongo.model.MongoBook;
import ru.otus.hw.mongo.model.MongoGenre;

@Configuration
public class MongoWritersConfiguration {

    @Bean
    public MongoItemWriter<MongoAuthor> mongoAuthorWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoAuthor>()
                .template(mongoTemplate)
                .collection("authors")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoBook> mongoBookWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoBook>()
                .template(mongoTemplate)
                .collection("books")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoGenre> mongoGenreWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoGenre>()
                .template(mongoTemplate)
                .collection("genres")
                .build();
    }
}
