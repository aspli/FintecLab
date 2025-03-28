package com.bootcamp.conta_service.exception;

public class ContaNaoExistenteException extends RuntimeException {
    public ContaNaoExistenteException(String message) {
        super(message);
    }
}
