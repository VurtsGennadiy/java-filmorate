package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        StringBuilder response = new StringBuilder();
        for (FieldError error : exception.getFieldErrors()) {
            response.append("Поле ");
            response.append(error.getField());
            response.append(" ");
            response.append(error.getDefaultMessage());
            response.append(System.lineSeparator());
        }
        log.warn("Invalid request", exception);
        return response.toString();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException exception) {
        log.warn("Invalid request", exception);
        exception.fillInStackTrace();
        return exception.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class, DuplicateDataException.class})
    public String handleException(Exception exception) {
        log.warn("Invalid request", exception);
        return exception.getMessage();
    }
}
