package org.example.comum;

// Situacoes possiveis de um partido (RN-CAN-6)
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
