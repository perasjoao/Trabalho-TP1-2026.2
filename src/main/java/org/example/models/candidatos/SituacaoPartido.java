package org.example.models.candidatos;

public enum SituacaoPartido {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    SituacaoPartido(String descricao) {
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
