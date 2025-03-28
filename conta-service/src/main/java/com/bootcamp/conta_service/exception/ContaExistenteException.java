package com.bootcamp.conta_service.exception;

public class ContaExistenteException extends RuntimeException {
    public ContaExistenteException(String message) {
        super(message);
    }

}
