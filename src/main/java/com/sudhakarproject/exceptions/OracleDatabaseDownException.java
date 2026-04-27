package com.sudhakarproject.exceptions;

public class OracleDatabaseDownException extends RuntimeException{
    public OracleDatabaseDownException(String message) {
        super(message);
    }
}
