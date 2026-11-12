package org.example.models.comum;

public enum Regiao {
    NORTE("Norte"),
    NORDESTE("Nordeste"),
    CENTRO_OESTE("Centro-Oeste"),
    SUDESTE("Sudeste"),
    SUL("Sul"),
    NACIONAL("Nacional");

    private final String descricao;

    Regiao(String descricao) {
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