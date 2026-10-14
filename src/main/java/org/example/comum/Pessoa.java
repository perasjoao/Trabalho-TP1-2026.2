package org.example.comum;

// Base abstrata para quem tem nome no sistema: Usuario, Entrevistador e Candidato.
// Cada subclasse define como se descreve (polimorfismo em getDescricao).
public abstract class Pessoa {

    private String nome;

    protected Pessoa(String nome) {
        setNome(nome);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public abstract String getDescricao();

    @Override
    public String toString() {
        return getDescricao();
    }
}
