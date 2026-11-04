package org.example.exceptions;

public class ColetaIncompletaException extends Exception {
    public ColetaIncompletaException(String mensagem) {
        super(mensagem);
    }
}