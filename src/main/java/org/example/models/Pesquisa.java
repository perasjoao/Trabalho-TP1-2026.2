package org.example.models;

public class Pesquisa {
    Entrevista[] entrevistas;
    String tipo;
    String situacao;
    String[] perguntas;
    String regiao;
    String[] parametros;
    int pesquisasFeitas;
    int pesquisasPrevistas;

    public float percentualFeito(){}

    public boolean antingiuMeta(){}

    public void adicionaPergunta(String pergunta){}

    public void excluiPergunta(String pergunta){}

    public String[] getPerguntas() {}
}
