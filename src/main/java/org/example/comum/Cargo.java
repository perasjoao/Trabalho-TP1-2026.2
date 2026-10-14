package org.example.comum;

// Cargos em disputa; usado por Pesquisas e Candidatos (RN-PES-3: cargo compativel)
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
