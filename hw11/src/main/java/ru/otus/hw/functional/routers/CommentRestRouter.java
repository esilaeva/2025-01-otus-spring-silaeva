package ru.otus.hw.functional.routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.functional.handlers.CommentRestHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;


@Configuration
public class CommentRestRouter {

    @Bean
    public RouterFunction<ServerResponse> routerComment(CommentRestHandler commentRestHandler) {

        return RouterFunctions
                .route(GET("/api/v2/comment/{id}"), commentRestHandler::getCommentByCommentId)
                .andRoute(GET("/api/v2/book/{bookId}/comment"), commentRestHandler::getCommentsByBookId)
                .andRoute(POST("/api/v2/comment"), commentRestHandler::insertComment)
                .andRoute(PUT("/api/v2/comment"), commentRestHandler::updateComment)
                .andRoute(DELETE("/api/v2/comment/{id}"), commentRestHandler::deleteCommentByCommentId);
    }
}
