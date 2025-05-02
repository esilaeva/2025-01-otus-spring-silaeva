package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.EntityToDtoMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final EntityToDtoMapper mapper;

    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .map(mapper::commentToCommentDto);
    }

    @Override
    public List<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(mapper::commentToCommentDto)
                .toList();
    }

    @Override
    public CommentDto create(String bookId, String commentContent) {
        validateInput(StringUtils.isBlank(bookId) || StringUtils.isBlank(commentContent),
                () -> ("Book id: %s or/and Comment content: %s are not applicable"
                        .formatted(bookId, commentContent)));

        var book = checkAndGetEntity(bookRepository::findById, Entity.BOOK.getName(), bookId);

        var newComment = commentRepository.save(new Comment(null, commentContent, book));

        return mapper.commentToCommentDto(newComment);
    }

    @Override
    public CommentDto update(String id, String newContent) {
        validateInput(StringUtils.isBlank(id) || StringUtils.isBlank(newContent),
                () -> ("Book id: %s or/and Comment content: %s are not applicable"
                        .formatted(id, newContent)));

        var comment = checkAndGetEntity(commentRepository::findById, Entity.COMMENT.getName(), id);

        comment.setContent(newContent);

        return mapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private static <T> T checkAndGetEntity(Function<String, Optional<T>> repositoryMethod,
                                           String entityName,
                                           String id) {
        return repositoryMethod.apply(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        NotFoundMessage.ENTITY.getMessage().formatted(entityName, id)));
    }

    private static void validateInput(boolean isInvalid, Supplier<String> messageSupplier) {
        if (isInvalid) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
}