package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Comment;
import ru.otus.hw.utils.TestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MongoDB based repository for working with Comments: ")
@DataMongoTest
class MongoCommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @DisplayName("should return a list of the comments for a given books")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var firstBook = TestUtils.books.get(0);
        List<Comment> expectedComments = mongoTemplate.findAll(Comment.class)
                .stream()
                .filter(comment -> comment.getBook().equals(firstBook))
                .toList();

        List<Comment> actualComments = repository.findByBookId(firstBook.getId());
        assertThat(actualComments)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedComments);
    }
}