package com.onus.backend.exception;
public class DuplicateApplicationException
        extends RuntimeException {

    public DuplicateApplicationException(String message) {
        super(message);
    }
}
