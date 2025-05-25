package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final EntityToDtoMapper entityToDtoMapper;


    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {

        return commentRepository.findById(id)
                .map(entityToDtoMapper::commentToCommentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findByBookId(long bookId) {

        return commentRepository.findByBookId(bookId)
                .stream()
                .map(entityToDtoMapper::commentToCommentDto)
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
        var book = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookId);
        var savedComment = commentRepository.save(new Comment(0, commentContent, book));

        return entityToDtoMapper.commentToCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto update(CommentUpdateDto commentUpdateDto) {
        if (commentUpdateDto == null) {
            throw new IllegalArgumentException("commentUpdateDto is null");
        }
        long id = commentUpdateDto.getId();
        String newContent = commentUpdateDto.getContent();
        var comment = checkAndGetEntity(commentRepository::findById, Entity.COMMENT.getName(), id);
        comment.setContent(newContent);

        return entityToDtoMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        var comment = checkAndGetEntity(commentRepository::findById, Entity.COMMENT.getName(), id);

        commentRepository.deleteById(comment.getId());
    }


    private static <T> T checkAndGetEntity(LongFunction<Optional<T>> repositoryMethod, String entityName, long id) {
        return repositoryMethod.apply(id)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundMessage.ENTITY.getMessage()
                        .formatted(entityName, id)));
    }
}
