package com.backbase.moviesdigger.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.backbase.moviesdigger.client.spec.model.Error;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class ApiExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> notFoundExceptionHandler(NotFoundException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(404);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> badRequestExceptionHandler(BadRequestException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(400);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Error> unauthorizedExceptionHandler(UnauthorizedException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(401);

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Error> conflictExceptionHandler(ConflictException ex) {
        Error error = new Error();
        error.setMessage(ex.getMessage());
        error.setCode(409);

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
