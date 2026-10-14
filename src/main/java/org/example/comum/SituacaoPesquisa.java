package org.example.comum;

// Situacoes de uma pesquisa (RF-PES-3)
public enum SituacaoPesquisa {
    PLANEJADA("Planejada"),
    EM_COLETA("Em coleta"),
    ENCERRADA("Encerrada"),
    PROCESSADA("Processada");

    private final String descricao;

    SituacaoPesquisa(String descricao) {
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
