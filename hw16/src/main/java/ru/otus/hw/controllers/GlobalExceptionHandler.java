package ru.otus.hw.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.EntityNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFoundException(EntityNotFoundException ex) {
        log.warn("{}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("errors/404", HttpStatus.NOT_FOUND);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        log.error("Exception: ", ex);
        ModelAndView modelAndView = new ModelAndView("errors/500", HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }
}
