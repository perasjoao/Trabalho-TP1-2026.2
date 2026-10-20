package org.example.models.candidatos;

public enum SituacaoCandidatura {
    PRE_CANDIDATO("Pré-candidato"),
    CANDIDATO("Candidato"),
    INATIVA("Candidatura inativa"),
    VALIDADA("Candidatura validada");

    private final String descricao;

    SituacaoCandidatura(String descricao) {
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