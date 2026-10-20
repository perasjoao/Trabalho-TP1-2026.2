package org.example.models.researchModels;

import java.util.List;

public class Pergunta extends ItemPesquisa {
    private String tipo;
    private List<String> opcoes;

    public Pergunta(String id, String enunciado, String tipo, List<String> opcoes) {
        super(id, enunciado);
        this.tipo = tipo;
        this.opcoes = opcoes;
    }

    public String getTipo() { return tipo; }
    public List<String> getOpcoes() { return opcoes; }
    public void setOpcoes(List<String> opcoes) { this.opcoes = opcoes; }
}