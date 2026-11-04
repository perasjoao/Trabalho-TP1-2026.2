package org.example.exceptions;

// Dado obrigatorio ausente ou invalido no cadastro de candidaturas e partidos
// (nome vazio, numero <= 0, sigla vazia, cargo ou situacao nulos)
public class DadoInvalidoException extends Exception {
    public DadoInvalidoException(String message) {
        super(message);
    }
}
