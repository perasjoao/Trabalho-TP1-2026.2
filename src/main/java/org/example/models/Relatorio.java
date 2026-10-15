package org.example.models;
import java.util.Map;
public class Relatorio {
    int totalApurado;
    Candidato candidato;
    Map<String,Float>[]estatisticas; //chave valor

    public int getTotalApurado() {
        return this.totalApurado;
    }

    public Map<String, Float>[] getEstatisticas() {
        return this.estatisticas;
    }


}
