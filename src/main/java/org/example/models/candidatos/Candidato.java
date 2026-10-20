package org.example.models.candidatos;

public class Candidato {

    private String nome;

    public Candidato(String nome){
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if(nome == null || nome.isBlank()) throw new IllegalArgumentException("O nome do candidato é obrigatório.");
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return getNome();
    }
}
