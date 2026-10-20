package org.example.models.candidatos;

import org.example.models.comum.Cargo;

//candidatura de um candidato a um cargo
//herda o numero de RegistroEleitoral e pertence a no maximo um partido
public class Candidatura extends RegistroEleitoral {

    private final Candidato candidato;
    private Cargo cargo;
    private SituacaoCandidatura situacao;
    private Partido partido;

    public Candidatura(Candidato candidato, int numero, Cargo cargo, SituacaoCandidatura situacao) {
        super(numero);
        if (candidato == null) {
            throw new IllegalArgumentException("A candidatura precisa de um candidato.");
        }
        this.candidato = candidato;
        setCargo(cargo);
        setSituacao(situacao);
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public Cargo getCargo() {
        return cargo;
    }

    //o cargo faz parte do cadastro
    public void setCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo da candidatura é obrigatório.");
        }
        this.cargo = cargo;
    }

    public SituacaoCandidatura getSituacao() {
        return situacao;
    }

    //gerenciar situacao da candidatura
    public void setSituacao(SituacaoCandidatura situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("A situação da candidatura é obrigatória.");
        }
        this.situacao = situacao;
    }

    public Partido getPartido() {
        return partido;
    }

    //so classes deste pacote podem chamar, manter coerencia
    void setPartido(Partido partido) {
        this.partido = partido;
    }

    //candidatura inativa nao conta como ativa
    @Override
    public boolean isAtivo() {
        return situacao != SituacaoCandidatura.INATIVA;
    }

    //so candidaturas confirmadas podem entrar em pesquisas
    public boolean podeParticiparDePesquisa() {
        return situacao == SituacaoCandidatura.CANDIDATO || situacao == SituacaoCandidatura.VALIDADA;
    }

    @Override
    public String getDescricao() {
        String sigla = (partido == null) ? "sem partido" : partido.getSigla();
        return candidato.getNome() + " (" + getNumero() + ", " + sigla + ") - " + cargo;
    }
}