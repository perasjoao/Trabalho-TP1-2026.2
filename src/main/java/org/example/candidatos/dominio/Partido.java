package org.example.candidatos.dominio;

import org.example.comum.SituacaoPartido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Partido ficticio. Um partido tem varios candidatos (RN-CAN-3).
public class Partido {

    private String nome;
    private String sigla;
    private int numero;
    private SituacaoPartido situacao;
    private final List<Candidato> candidatos = new ArrayList<>();

    public Partido(String nome, String sigla, int numero, SituacaoPartido situacao) {
        this.nome = nome;
        this.sigla = sigla;
        this.numero = numero;
        this.situacao = situacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public SituacaoPartido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPartido situacao) {
        this.situacao = situacao;
    }

    // RN-CAN-8
    public boolean isAtivo() {
        return situacao == SituacaoPartido.ATIVO;
    }

    public List<Candidato> getCandidatos() {
        return Collections.unmodifiableList(candidatos);
    }

    // RN-CAN-3: se o candidato ja tinha partido, sai dele antes de entrar neste
    public void adicionarCandidato(Candidato candidato) {
        if (candidato.getPartido() != null) {
            candidato.getPartido().removerCandidato(candidato);
        }
        candidatos.add(candidato);
        candidato.setPartido(this);
    }

    public void removerCandidato(Candidato candidato) {
        if (candidatos.remove(candidato)) {
            candidato.setPartido(null);
        }
    }

    public boolean possuiCandidatos() {
        return !candidatos.isEmpty();
    }

    @Override
    public String toString() {
        return sigla + " - " + nome + " (" + numero + ")";
    }
}
