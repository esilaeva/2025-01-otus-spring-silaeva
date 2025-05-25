package ru.otus.hw.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.dto.ErrorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(EntityNotFoundException ex) {

        log.warn("{}", ex.getMessage());

        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {

        log.error("Exception: {}", ex.getMessage());

        ErrorDto errorDto = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
