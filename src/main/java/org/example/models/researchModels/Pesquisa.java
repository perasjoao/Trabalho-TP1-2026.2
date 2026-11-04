package org.example.models.researchModels;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pesquisa {
    private final String  codigo;
    private String nome;
    private String cargo;
    private String tipo;
    private String situacao;
    private String abrangencia;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int entrevistasPrevistas;
    private String metodologia;
    private List<Pergunta> perguntas;

    public Pesquisa(String codigo, String nome, String cargo, String tipo, String situacao,
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
        this.metodologia = metodologia;
        this.perguntas = (perguntas != null) ? perguntas : new ArrayList<>();
    }

    // Getters e Setters
    public String getCodigo() { return codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

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

    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }

    public List<Pergunta> getPerguntas() { return perguntas; }
    public void setPerguntas(List<Pergunta> perguntas) { this.perguntas = perguntas; }
}