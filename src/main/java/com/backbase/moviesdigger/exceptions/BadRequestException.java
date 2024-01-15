package com.backbase.moviesdigger.exceptions;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }
}
