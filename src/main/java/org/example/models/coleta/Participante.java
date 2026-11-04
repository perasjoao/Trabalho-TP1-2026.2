package org.example.models.coleta;
import java.io.Serializable;

public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String faixaEtaria;
    private String escolaridade;
    private String genero;
    private String regiao;
    private String faixaRenda;

    public Participante(String codigo, String faixaEtaria, String escolaridade, String genero, String regiao, String faixaRenda) {
        this.codigo = codigo;
        this.faixaEtaria = faixaEtaria;
        this.escolaridade = escolaridade;
        this.genero = genero;
        this.regiao = regiao;
        this.faixaRenda = faixaRenda;
    }

    public String getCodigo() { return codigo; }
    public String getFaixaEtaria() { return faixaEtaria; }
    public String getEscolaridade() { return escolaridade; }
    public String getGenero() { return genero; }
    public String getRegiao() { return regiao; }
    public String getFaixaRenda() { return faixaRenda; }
}