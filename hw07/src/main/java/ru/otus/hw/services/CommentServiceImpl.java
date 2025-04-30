package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

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
    public Comment create(long bookId, String commentContent) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException(NotFoundMessage.BOOK.getMessage().formatted(bookId)));
        return commentRepository.save(new Comment(0, commentContent, book));
    }

    @Override
    @Transactional
    public Comment update(long id, String newContent) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id)));
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id)));
        commentRepository.deleteById(comment.getId());
    }
}
