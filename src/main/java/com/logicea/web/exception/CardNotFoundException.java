package com.logicea.web.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CardNotFoundException(final String message) {
        super(message);
    }

    public CardNotFoundException(final Throwable cause) {
        super(cause);
    }
}