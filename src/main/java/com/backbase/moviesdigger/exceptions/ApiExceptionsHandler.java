package com.backbase.moviesdigger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.backbase.moviesdigger.client.spec.model.Error;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> badRequestExceptionHandler(BadRequestException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(400);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(400);

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Error> unauthorizedExceptionHandler(UnauthorizedException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(401);

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> notFoundExceptionHandler(NotFoundException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(404);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Error> conflictExceptionHandler(ConflictException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(409);

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Error> genericExceptionsFromServer(GeneralException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(500);

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
