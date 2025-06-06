package ru.otus.hw.functional.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class GenreRestRouter {

    @Bean
    public RouterFunction<ServerResponse> getAllGenresRoute(GenreService genreService) {

        return route(GET("/api/v2/genre"),
                request -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(genreService.findAll(), GenreDto.class)
        );
    }
}
