package org.example.models;

import java.util.List;
import java.util.Map;

public class Estatisticas {
    String parametro;
    Map<String,Float> estatistica;
    public Estatisticas(String parametro, Map<String,Float> dados){
        this.parametro=parametro;
        this.estatistica= dados;
    }

    public Map<String, Float> getEstatistica() {
        return estatistica;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }
}
