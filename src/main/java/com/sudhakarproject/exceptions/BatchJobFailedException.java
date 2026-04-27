package com.sudhakarproject.exceptions;

public class BatchJobFailedException extends RuntimeException{
    public BatchJobFailedException(String message, Exception e) {
        super(message);
    }
}
