package com.bootcamp.conta_service.exception;

public class PixNegativoException extends RuntimeException {
    public PixNegativoException(String message) {
        super(message);
    }
}
