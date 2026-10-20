package org.example.models.candidatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//herda o numero de RegistroEleitoral
public class Partido extends RegistroEleitoral {

    private String nome;
    private String sigla;
    private SituacaoPartido situacao;
    private final List<Candidatura> candidaturas = new ArrayList<>();

    public Partido(String nome, String sigla, int numero, SituacaoPartido situacao) {
        super(numero);
        setNome(nome);
        setSigla(sigla);
        setSituacao(situacao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do partido é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        if (sigla == null || sigla.isBlank()) {
            throw new IllegalArgumentException("A sigla do partido é obrigatória.");
        }
        this.sigla = sigla.trim().toUpperCase();
    }

    public SituacaoPartido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPartido situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("A situação do partido é obrigatória.");
        }
        this.situacao = situacao;
    }

    public List<Candidatura> getCandidaturas() {
        return Collections.unmodifiableList(candidaturas);
    }

    //se a candidatura ja tinha partido, sai dele antes de entrar neste
    public void adicionarCandidatura(Candidatura candidatura) {
        if (candidatura.getPartido() == this) {
            return;
        }
        if (candidatura.getPartido() != null) {
            candidatura.getPartido().removerCandidatura(candidatura);
        }
        candidaturas.add(candidatura);
        candidatura.setPartido(this);
    }

    public void removerCandidatura(Candidatura candidatura) {
        if (candidaturas.remove(candidatura)) {
            candidatura.setPartido(null);
        }
    }

    //impede a exclusao de partido com candidaturas
    public boolean possuiCandidaturas() {
        return !candidaturas.isEmpty();
    }

    //so conta como ativo se a situacao for ATIVO
    @Override
    public boolean isAtivo() {
        return situacao == SituacaoPartido.ATIVO;
    }

    @Override
    public String getDescricao() {
        return sigla + " - " + nome + " (" + getNumero() + ")";
    }
}