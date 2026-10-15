package org.example.models;
import java.util.Map;
public class Relatorio {
    int totalApurado;
    Map<String,Float>[]estatisticas; //chave valor

    public int getTotalApurado() {
        return this.totalApurado;
    }

    public Map<String, Float>[] getEstatisticas() {
        return this.estatisticas;
    }

    public boolean exportaRelatorio(){}

    public void filtrarRelatorio(){}
}
