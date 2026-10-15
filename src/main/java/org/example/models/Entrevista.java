package org.example.models;

public class Entrevista {
    Entrevistador entrevistador;
    Entrevistado entrevistado;
    String pergunta;
    String resposta;
    Pesquisa pesquisa;

    void setResposta(String resposta){
        this.resposta=resposta;
    }
    String getResposta(){
        return this.resposta;
    }
}
