package org.example.models.candidatos;

import org.example.exceptions.DadoInvalidoException;

import java.io.Serializable;

public class Candidato implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;

    public Candidato(String nome) throws DadoInvalidoException {
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    //o nome e obrigatorio
    public void setNome(String nome) throws DadoInvalidoException {
        if(nome == null || nome.isBlank()) throw new DadoInvalidoException("O nome do candidato é obrigatório.");
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return getNome();
    }
}
