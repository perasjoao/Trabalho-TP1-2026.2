package org.example.exceptions;

// RN-CAN-1: ja existe partido com a mesma sigla ou o mesmo numero
public class PartidoDuplicadoException extends Exception {
    public PartidoDuplicadoException(String message) {
        super(message);
    }
}
