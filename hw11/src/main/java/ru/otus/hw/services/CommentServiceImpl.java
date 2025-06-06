package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.enums.Entity;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.ModelToDtoMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final ModelToDtoMapper mapper;

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException(NotFoundMessage.COMMENT.getMessage().formatted(id))))
                .map(mapper::commentToCommentDto);
    }

    @Override
    public Flux<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId)
                .map(mapper::commentToCommentDto);
    }

    @Override
    public Mono<CommentDto> create(CommentCreateDto commentCreateDto) {
        validateInput(StringUtils.isBlank(commentCreateDto.bookId())
                        || StringUtils.isBlank(commentCreateDto.commentContent()),
                () -> ("Book id: %s or/and Comment content: %s are not applicable"
                        .formatted(commentCreateDto.bookId(), commentCreateDto.commentContent())));

        return fetchAndValidateEntity(bookRepository::findById, Entity.BOOK.getName(), commentCreateDto.bookId())
                .flatMap(book -> {
                    Comment commentToSave = new Comment();
                    commentToSave.setContent(commentCreateDto.commentContent());
                    commentToSave.setBook(book);
                    return commentRepository.save(commentToSave);
                }).mapNotNull(mapper::commentToCommentDto);

    }

    @Override
    public Mono<CommentDto> update(CommentUpdateDto commentUpdateDto) {
        validateInput(StringUtils.isBlank(commentUpdateDto.id())
                        || StringUtils.isBlank(commentUpdateDto.commentContent()),
                () -> ("Comment id: %s or/and new Comment content: %s are not applicable"
                        .formatted(commentUpdateDto.id(), commentUpdateDto.commentContent())));

        return fetchAndValidateEntity(commentRepository::findById, Entity.COMMENT.getName(), commentUpdateDto.id())
                .flatMap(comment -> {
                    comment.setContent(commentUpdateDto.commentContent());
                    return commentRepository.save(comment);
                }).mapNotNull(mapper::commentToCommentDto);
    }

    @Override
    public Mono<Void> deleteById(String id) {

        return commentRepository.deleteById(id);
    }


    private static <T> Mono<T> fetchAndValidateEntity(Function<String, Mono<T>> repositoryMethod,
                                                      String entityName,
                                                      String id) {
        return repositoryMethod.apply(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        NotFoundMessage.ENTITY.getMessage().formatted(entityName, id))));
    }

    private static void validateInput(boolean isInvalid, Supplier<String> messageSupplier) {
        if (isInvalid) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
}