package org.example.comum;

// Situacoes possiveis de uma candidatura (RN-CAN-5)
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
