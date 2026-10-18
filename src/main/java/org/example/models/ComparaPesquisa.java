package org.example.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ComparaPesquisa {
    Relatorio r1;
    Relatorio r2;
    List<Map<String, Map<String, Float>>> comp;

    public ComparaPesquisa(Relatorio r1, Relatorio r2, Estatisticas[] est){
        this.r1=r1;
        this.r2=r2;
        //depois dos calculos this.comp=Resultados;
    }
}
