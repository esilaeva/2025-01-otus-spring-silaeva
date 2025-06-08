package ru.otus.hw.functional.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class BookRestHandler {

    private final BookService bookService;

    public Mono<ServerResponse> getAllBooks(ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookService.findAll(), BookDto.class);
    }

    public Mono<ServerResponse> getBookById(ServerRequest request) {

        return bookService.findById(request.pathVariable("id"))
                .flatMap(bookDto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(bookDto))
                                .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> insertBook(ServerRequest request) {

        return request.bodyToMono(BookCreateDto.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is missing.")))
                .flatMap(bookCreateDto -> bookService.create(bookCreateDto)
                        .flatMap(bookDto -> ServerResponse.accepted()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(bookDto)))
                ).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                )
                .onErrorResume(org.springframework.core.codec.DecodingException.class, e ->
                        ServerResponse.badRequest().bodyValue("Malformed request body: " + e.getMessage()));
    }

    public Mono<ServerResponse> updateBook(ServerRequest request) {

        return request.bodyToMono(BookUpdateDto.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is missing.")))
                .flatMap(bookUpdateDto ->
                        bookService.findById(bookUpdateDto.id())
                                .flatMap(existingBook -> bookService.update(bookUpdateDto))
                                .flatMap(updatedBook -> ServerResponse.accepted()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(fromValue(updatedBook)))
                                .switchIfEmpty(ServerResponse.notFound().build())
                )
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                )
                .onErrorResume(org.springframework.core.codec.DecodingException.class, e ->
                        ServerResponse.badRequest().bodyValue("Malformed request body: " + e.getMessage()));
    }

    public Mono<ServerResponse> deleteBookById(ServerRequest request) {

        return bookService.findById(request.pathVariable("id"))
                .flatMap(existingBook -> bookService.deleteById(existingBook.id()))
                .then(ServerResponse.noContent().build());
    }
}
