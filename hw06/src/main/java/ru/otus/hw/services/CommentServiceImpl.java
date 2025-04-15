package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static ru.otus.hw.repositories.JpaBookRepository.BOOK_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    public static final String COMMENT_NOT_FOUND = "Comment with id %d not found";

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;


    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    @Transactional
    public Comment insert(long bookId, String commentContent) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException(BOOK_NOT_FOUND.formatted(bookId)));
        return commentRepository.save(new Comment(0, commentContent, book));
    }

    @Override
    @Transactional
    public Comment update(long id, String newContent) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(COMMENT_NOT_FOUND.formatted(id)));
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(COMMENT_NOT_FOUND.formatted(id)));
        commentRepository.deleteById(comment.getId());
    }
}
