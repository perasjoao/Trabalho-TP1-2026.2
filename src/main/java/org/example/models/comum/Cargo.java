package org.example.models.comum;

public enum Cargo {
    PRESIDENTE("Presidente"),
    GOVERNADOR("Governador"),
    SENADOR("Senador"),
    DEPUTADO_FEDERAL("Deputado Federal"),
    DEPUTADO_ESTADUAL("Deputado Estadual");

    private final String descricao;

    Cargo(String descricao) {
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