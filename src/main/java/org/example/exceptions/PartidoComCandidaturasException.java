package org.example.exceptions;

// RN-CAN-9: partido com candidaturas associadas nao pode ser excluido
public class PartidoComCandidaturasException extends Exception {
    public PartidoComCandidaturasException(String message) {
        super(message);
    }
}
