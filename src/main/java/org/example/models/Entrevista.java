package org.example.models;

import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.models.coleta.Resposta;

import java.util.List;

public class Entrevista {
    private String idEntrevista;
    private String codigoPesquisa;
    private Entrevistador entrevistador;
    private Participante participante;
    private List<Resposta> respostas;

    public Entrevista() {
    }

    public Entrevista(String idEntrevista, String codigoPesquisa, Entrevistador entrevistador, Participante participante, List<Resposta> respostas) {
        this.idEntrevista = idEntrevista;
        this.codigoPesquisa = codigoPesquisa;
        this.entrevistador = entrevistador;
        this.participante = participante;
        this.respostas = respostas;
    }

    public String getIdEntrevista() { return idEntrevista; }
    public void setIdEntrevista(String idEntrevista) { this.idEntrevista = idEntrevista; }

    public String getCodigoPesquisa() { return codigoPesquisa; }
    public void setCodigoPesquisa(String codigoPesquisa) { this.codigoPesquisa = codigoPesquisa; }

    public Entrevistador getEntrevistador() { return entrevistador; }
    public void setEntrevistador(Entrevistador entrevistador) { this.entrevistador = entrevistador; }

    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }

    public List<Resposta> getRespostas() { return respostas; }
    public void setRespostas(List<Resposta> respostas) { this.respostas = respostas; }
}