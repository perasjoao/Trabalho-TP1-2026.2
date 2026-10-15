package org.example.models;

public class Partido {
    int numero;
    String nome;
    Candidato[] candidatos;
    String sigla;

    public Candidato[] getCandidatos(){
        return this.candidatos;
    }

    public int getNumero() {
        return this.numero;
    }

    public String getSigla() {
        return this.sigla;
    }
}
