package ru.otus.hw.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.otus.hw.dto.ErrorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(EntityNotFoundException ex) {

        log.warn("{}", ex.getMessage());

        ErrorDto errorDto = new ErrorDto(HttpStatus.NOT_FOUND.value(),
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {

        String toUserErrorMessage = logFullExceptionAndCreateErrorMessage(ex);

        ErrorDto errorDto = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Collections.singletonList(toUserErrorMessage)
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRunTimeException(RuntimeException ex) {

        String toUserErrorMessage = logFullExceptionAndCreateErrorMessage(ex);

        ErrorDto errorDto = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Collections.singletonList(toUserErrorMessage)
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errorsList = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.warn("The following errors are occurred: {}", errorsList);

        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST.value(), errorsList);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    private String logFullExceptionAndCreateErrorMessage(Exception ex) {

        log.error("Exception: ", ex);

        return Arrays.stream(ex.getMessage().split(":", 2))
                .findFirst()
                .orElse("Internal server error was occurred");
    }

}
