package org.example.models.researchModels;

import java.util.List;

public class Pesquisa {
    private String codigo;
    private String nome;
    private String cargo;
    private String situacao;
    private List<Pergunta> perguntas;

    public Pesquisa(String codigo, String nome, String cargo, String situacao, List<Pergunta> perguntas) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargo = cargo;
        this.situacao = situacao;
        this.perguntas = perguntas;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public List<Pergunta> getPerguntas() { return perguntas; }
}