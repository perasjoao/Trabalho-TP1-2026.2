package org.example.administracao.dominio;

// Categorias de parametros configuraveis (RF-ADM-6)
public enum TipoParametro {
    FAIXA_ETARIA("Faixa etária"),
    ESCOLARIDADE("Escolaridade"),
    REGIAO("Região"),
    FAIXA_RENDA("Faixa de renda");

    private final String descricao;

    TipoParametro(String descricao) {
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
