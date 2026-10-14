package org.example.coleta.dominio;

// Participante ficticio e suas caracteristicas de amostra (RF-COL-1).
// Nao tem nome: e identificado so pelo codigo, sem dados pessoais reais.
public class Participante {

    private final String codigo;
    private final String faixaEtaria;
    private final String escolaridade;
    private final String genero;
    private final String regiao;
    private final String faixaRenda;

    public Participante(String codigo, String faixaEtaria, String escolaridade,
                        String genero, String regiao, String faixaRenda) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código do participante é obrigatório.");
        }
        this.codigo = codigo;
        this.faixaEtaria = faixaEtaria;
        this.escolaridade = escolaridade;
        this.genero = genero;
        this.regiao = regiao;
        this.faixaRenda = faixaRenda;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getFaixaEtaria() {
        return faixaEtaria;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public String getGenero() {
        return genero;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getFaixaRenda() {
        return faixaRenda;
    }

    @Override
    public String toString() {
        return codigo + " (" + faixaEtaria + ", " + escolaridade + ", " + genero + ", " + regiao + ", " + faixaRenda + ")";
    }
}
