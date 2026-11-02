package org.example.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparaPesquisa {
    private Relatorio r1;
    private Relatorio r2;
    private List<Estatisticas> comparacoes;

    public ComparaPesquisa(Relatorio r1, Relatorio r2, List<Estatisticas> comparacoes) {
        this.r1 = r1;
        this.r2 = r2;
        this.comparacoes = (comparacoes != null) ? comparacoes : new ArrayList<>();
    }

    public Relatorio getR1() {
        return r1;
    }

    public void setR1(Relatorio r1) {
        this.r1 = r1;
    }

    public Relatorio getR2() {
        return r2;
    }

    public void setR2(Relatorio r2) {
        this.r2 = r2;
    }

    public List<Estatisticas> getComparacoes() {
        return Collections.unmodifiableList(comparacoes);
    }

    public void setComparacoes(List<Estatisticas> comparacoes) {
        this.comparacoes = comparacoes;
    }
}