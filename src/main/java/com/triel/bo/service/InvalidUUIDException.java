package com.triel.bo.service;


public class InvalidUUIDException extends Exception {

    public InvalidUUIDException() {
    }

    public InvalidUUIDException(String message) {
        super(message);
    }

    public InvalidUUIDException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUUIDException(Throwable cause) {
        super(cause);
    }

    public InvalidUUIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}