package ru.otus.hw.functional.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class AuthorRestRouter {

    @Bean
    public RouterFunction<ServerResponse> getAllAuthorsRoute(AuthorService authorService) {

        return route(GET("/api/v2/author"),
                request -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authorService.findAll(), AuthorDto.class)
        );
    }
}
