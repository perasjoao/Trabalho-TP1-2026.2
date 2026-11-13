package org.example.models.researchModels;

import org.example.models.candidatos.Candidatura;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pesquisa {
    private final String codigo;
    private String nome;
    private Cargo cargo;
    private String tipo;
    private String situacao;
    private String abrangencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int entrevistasPrevistas;
    private int entrevistasRealizadas;
    private PlanoAmostral planoAmostral;
    private List<Pergunta> perguntas;
    private List<Regiao> regioesAssociadas;
    private List<Candidatura> candidaturasAssociadas;
    private List<Integer> numerosCandidaturasSalvas; // Apoio para reconstrução após leitura do TXT

    public Pesquisa(String codigo, String nome, Cargo cargo, String tipo, String situacao,
                    String abrangencia, LocalDate dataInicio, LocalDate dataFim,
                    int entrevistasPrevistas, String metodologia, List<Pergunta> perguntas) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargo = cargo;
        this.tipo = tipo;
        this.situacao = situacao;
        this.abrangencia = abrangencia;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.entrevistasPrevistas = entrevistasPrevistas;
        this.entrevistasRealizadas = 0;
        this.planoAmostral = new PlanoAmostral(metodologia);
        this.perguntas = (perguntas != null) ? perguntas : new ArrayList<>();
        this.regioesAssociadas = new ArrayList<>();
        this.candidaturasAssociadas = new ArrayList<>();
        this.numerosCandidaturasSalvas = new ArrayList<>();
    }

    public double getPercentualConclusao() {
        if (entrevistasPrevistas <= 0) return 0.0;
        return ((double) entrevistasRealizadas / entrevistasPrevistas) * 100.0;
    }

    // Getters e Setters
    public String getCodigo() { return codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getAbrangencia() { return abrangencia; }
    public void setAbrangencia(String abrangencia) { this.abrangencia = abrangencia; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public int getEntrevistasPrevistas() { return entrevistasPrevistas; }
    public void setEntrevistasPrevistas(int entrevistasPrevistas) { this.entrevistasPrevistas = entrevistasPrevistas; }

    public int getEntrevistasRealizadas() { return entrevistasRealizadas; }
    public void setEntrevistasRealizadas(int entrevistasRealizadas) { this.entrevistasRealizadas = entrevistasRealizadas; }

    public String getMetodologia() {
        return planoAmostral != null ? planoAmostral.getDescricaoMetodologia() : "";
    }
    public void setMetodologia(String metodologia) {
        if (this.planoAmostral == null) {
            this.planoAmostral = new PlanoAmostral(metodologia);
        } else {
            this.planoAmostral.setDescricaoMetodologia(metodologia);
        }
    }

    public PlanoAmostral getPlanoAmostral() { return planoAmostral; }
    public void setPlanoAmostral(PlanoAmostral planoAmostral) { this.planoAmostral = planoAmostral; }

    public List<Pergunta> getPerguntas() { return perguntas; }
    public void setPerguntas(List<Pergunta> perguntas) { this.perguntas = perguntas; }

    @Override
    public String toString() {
        return this.codigo + " - " + this.nome;
    }
}