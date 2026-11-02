package org.example.exceptions;

public class PesquisaInvalida extends RuntimeException {
    public PesquisaInvalida(String message) {
        super(message);
    }
}
