package org.example.models.researchModels;

import java.util.ArrayList;
import java.util.List;

public class Pergunta extends ItemPesquisa {
    private String tipo;
    private List<OpcaoResposta> opcoes;

    public Pergunta(String id, String enunciado, String tipo, List<OpcaoResposta> opcoes) {
        super(id, enunciado);
        this.tipo = tipo;
        this.opcoes = (opcoes != null) ? opcoes : new ArrayList<>();
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<OpcaoResposta> getOpcoes() { return opcoes; }
    public void setOpcoes(List<OpcaoResposta> opcoes) { this.opcoes = opcoes; }

    public void adicionarOpcao(OpcaoResposta opcao) {
        if (opcao != null) {
            this.opcoes.add(opcao);
        }
    }

    public void removerOpcao(OpcaoResposta opcao) {
        this.opcoes.remove(opcao);
    }
}