package ru.otus.hw.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Data
@Document(collection = "books")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"mongoAuthor", "mongoGenres"})
public class MongoBook {

    @Id
    private String id;

    private String title;

    @Field(name = "author")
    private MongoAuthor mongoAuthor;

    @Field(name = "genres")
    private Set<MongoGenre> mongoGenres;
}
