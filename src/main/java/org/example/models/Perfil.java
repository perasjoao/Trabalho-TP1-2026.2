package org.example.models;

public enum Perfil {
    ADMINISTRADOR("Administrador"),
    PESQUISADOR("Pesquisador"),
    ANALISTA("Analista");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}