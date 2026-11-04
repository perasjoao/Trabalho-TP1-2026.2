package org.example.exceptions;

// RN-CAN-2: ja existe candidatura com o mesmo numero para o mesmo cargo
public class CandidaturaDuplicadaException extends Exception {
    public CandidaturaDuplicadaException(String message) {
        super(message);
    }
}
