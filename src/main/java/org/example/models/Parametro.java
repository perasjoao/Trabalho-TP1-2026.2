package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Parametro {
    private List<String> faixasEtarias;
    private List<String> escolaridades;
    private List<String> regioes;
    private List<String> faixasRenda;

    public Parametro() {
        this.faixasEtarias = new ArrayList<>();
        this.escolaridades = new ArrayList<>();
        this.regioes = new ArrayList<>();
        this.faixasRenda = new ArrayList<>();
    }

    public List<String> getFaixasEtarias() {
        return faixasEtarias;
    }

    public void setFaixasEtarias(List<String> faixasEtarias) {
        this.faixasEtarias = faixasEtarias;
    }

    public List<String> getEscolaridades() {
        return escolaridades;
    }

    public void setEscolaridades(List<String> escolaridades) {
        this.escolaridades = escolaridades;
    }

    public List<String> getRegioes() {
        return regioes;
    }

    public void setRegioes(List<String> regioes) {
        this.regioes = regioes;
    }

    public List<String> getFaixasRenda() {
        return faixasRenda;
    }

    public void setFaixasRenda(List<String> faixasRenda) {
        this.faixasRenda = faixasRenda;
    }
}