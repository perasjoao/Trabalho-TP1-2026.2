package org.example.comum;

// Respostas que nao escolhem nenhuma opcao (RF-COL-5); contadas a parte na analise (RN-ANA-2)
public enum RespostaEspecial {
    NAO_SABE("Não sabe"),
    NAO_RESPONDEU("Não respondeu"),
    NENHUMA_DAS_OPCOES("Nenhuma das opções");

    private final String descricao;

    RespostaEspecial(String descricao) {
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
