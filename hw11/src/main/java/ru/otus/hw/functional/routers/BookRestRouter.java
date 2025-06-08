package ru.otus.hw.functional.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.functional.handlers.BookRestHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;


@Configuration
public class BookRestRouter {

    @Bean
    public RouterFunction<ServerResponse> routeBook(BookRestHandler bookRestHandler) {

        return RouterFunctions
                .route(GET("/api/v2/book"), bookRestHandler::getAllBooks)
                .andRoute(GET("/api/v2/book/{id}"), bookRestHandler::getBookById)
                .andRoute(POST("/api/v2/book"), bookRestHandler::insertBook)
                .andRoute(PUT("/api/v2/book"), bookRestHandler::updateBook)
                .andRoute(DELETE("/api/v2/book/{id}"), bookRestHandler::deleteBookById);
    }
}