package ru.otus.hw.functional.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentCreateDto;
import ru.otus.hw.dto.CommentUpdateDto;
import ru.otus.hw.services.CommentService;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class CommentRestHandler {

    private final CommentService commentService;

    public Mono<ServerResponse> getCommentByCommentId(ServerRequest request) {

        return commentService.findById(request.pathVariable("id"))
                .flatMap(commentDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(commentDto)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getCommentsByBookId(ServerRequest request) {

        return commentService.findByBookId(request.pathVariable("bookId"))
                .collectList()
                .flatMap(commentDtoList -> commentDtoList.isEmpty()
                        ? ServerResponse.noContent().build()
                        : ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(commentDtoList)));
    }

    public Mono<ServerResponse> insertComment(ServerRequest request) {

        return request.bodyToMono(CommentCreateDto.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is missing or invalid.")))
                .flatMap(commentCreateDto -> commentService.create(commentCreateDto)
                        .flatMap(commentDto -> ServerResponse.accepted()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(commentDto)))
                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                )
                .onErrorResume(org.springframework.core.codec.DecodingException.class, e ->
                        ServerResponse.badRequest().bodyValue("Malformed request body: " + e.getMessage()));
    }

    public Mono<ServerResponse> updateComment(ServerRequest request) {

        return request.bodyToMono(CommentUpdateDto.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is missing")))
                .flatMap(commentUpdateDto ->
                        commentService.findById(commentUpdateDto.id())
                                .flatMap(existingComment -> commentService.update(commentUpdateDto))
                                .flatMap(updatedComment -> ServerResponse.accepted()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(updatedComment)))
                                .switchIfEmpty(ServerResponse.notFound().build())
                )
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                )
                .onErrorResume(org.springframework.core.codec.DecodingException.class, e ->
                        ServerResponse.badRequest().bodyValue("Malformed request body: " + e.getMessage()));
    }

    public Mono<ServerResponse> deleteCommentByCommentId(ServerRequest request) {

        return commentService.findById(request.pathVariable("id"))
                .flatMap(existingComment -> commentService.deleteById(existingComment.id()))
                .then(ServerResponse.noContent().build());
    }
}