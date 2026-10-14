package org.example.comum;

// Tipos de pesquisa (RF-PES-2)
public enum TipoPesquisa {
    INTENCAO_DE_VOTO("Intenção de voto"),
    REJEICAO("Rejeição"),
    CONHECIMENTO("Conhecimento"),
    SEGUNDO_TURNO("Segundo turno simulado");

    private final String descricao;

    TipoPesquisa(String descricao) {
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
