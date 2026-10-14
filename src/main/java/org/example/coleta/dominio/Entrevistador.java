package org.example.coleta.dominio;

import org.example.comum.Pessoa;

// Entrevistador ficticio (RF-COL-1). Herda o nome de Pessoa.
public class Entrevistador extends Pessoa {

    private final String codigo;

    public Entrevistador(String codigo, String nome) {
        super(nome);
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código do entrevistador é obrigatório.");
        }
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return codigo + " - " + getNome();
    }
}
