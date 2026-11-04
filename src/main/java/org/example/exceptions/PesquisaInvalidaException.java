package org.example.exceptions;

public class PesquisaInvalidaException extends RuntimeException {
    public PesquisaInvalidaException(String message) {
        super(message);
    }
}
