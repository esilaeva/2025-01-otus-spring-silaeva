package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(long id);

    List<Comment> findByBookId(long bookId);

    Comment create(long bookId, String commentContent);

    Comment update(long id, String newContent);

    void deleteById(long id);


}
