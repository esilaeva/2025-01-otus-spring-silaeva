package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
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

    private final EntityToDtoMapper mapper;


    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {

        return commentRepository.findById(id)
                .map(mapper::commentToCommentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {

        return commentRepository.findByBookId(bookId)
                .stream()
                .map(mapper::commentToCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(CommentCreateDto commentCreateDto) {
        if (commentCreateDto == null) {
            throw new IllegalArgumentException("commentCreateDto is null");
        }
        long bookId = commentCreateDto.getBookId();
        String commentContent = commentCreateDto.getContent();

        var book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException(NotFoundMessage.BOOK.getMessage().formatted(bookId)));

        var savedComment = commentRepository.save(new Comment(0, commentContent, book));

        return mapper.commentToCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto update(CommentUpdateDto commentUpdateDto) {
        if (commentUpdateDto == null) {
            throw new IllegalArgumentException("commentUpdateDto is null");
        }
        long id = commentUpdateDto.getId();
        String newContent = commentUpdateDto.getContent();

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id)));

        comment.setContent(newContent);

        return mapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id)));
        commentRepository.deleteById(comment.getId());
    }
}
