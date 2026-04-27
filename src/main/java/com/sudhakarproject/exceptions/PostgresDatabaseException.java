package com.sudhakarproject.exceptions;

public class PostgresDatabaseException extends RuntimeException{
    public PostgresDatabaseException(String message) {
        super(message);
    }
}
