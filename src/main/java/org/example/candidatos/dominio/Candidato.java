package org.example.candidatos.dominio;

import org.example.comum.Cargo;
import org.example.comum.Pessoa;
import org.example.comum.SituacaoCandidatura;

// Candidato ficticio. Herda o nome de Pessoa; pertence a no maximo um partido (RN-CAN-3).
public class Candidato extends Pessoa {

    private int numero;
    private Cargo cargo;
    private SituacaoCandidatura situacao;
    private Partido partido;

    public Candidato(String nome, int numero, Cargo cargo, SituacaoCandidatura situacao) {
        super(nome);
        setNumero(numero);
        setCargo(cargo);
        setSituacao(situacao);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if (numero <= 0) {
            throw new IllegalArgumentException("O número do candidato deve ser positivo.");
        }
        this.numero = numero;
    }

    public Cargo getCargo() {
        return cargo;
    }

    // RN-CAN-4: o cargo faz parte do cadastro
    public void setCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo do candidato é obrigatório.");
        }
        this.cargo = cargo;
    }

    public SituacaoCandidatura getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCandidatura situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("A situação da candidatura é obrigatória.");
        }
        this.situacao = situacao;
    }

    public Partido getPartido() {
        return partido;
    }

    // chamados pelo Partido, que mantem as duas pontas da associacao coerentes
    void setPartido(Partido partido) {
        this.partido = partido;
    }

    // RN-CAN-7: candidatura inativa nao conta como ativa
    public boolean isCandidaturaAtiva() {
        return situacao != SituacaoCandidatura.INATIVA;
    }

    // RN-PES-5: so candidaturas confirmadas entram em pesquisas
    public boolean podeParticiparDePesquisa() {
        return situacao == SituacaoCandidatura.CANDIDATO || situacao == SituacaoCandidatura.VALIDADA;
    }

    @Override
    public String getDescricao() {
        String sigla = partido == null ? "sem partido" : partido.getSigla();
        return getNome() + " (" + numero + ", " + sigla + ") - " + cargo;
    }
}
