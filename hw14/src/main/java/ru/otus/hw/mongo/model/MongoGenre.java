package ru.otus.hw.mongo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "genres")
@AllArgsConstructor
@NoArgsConstructor
public class MongoGenre {

    @Id
    private String id;

    private String name;

}
