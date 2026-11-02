package org.example.models;

import org.example.models.researchModels.Pesquisa;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Relatorio {
   private List<Estatisticas> estatisticas;
   private Pesquisa pesquisa;
   private String titulo;

   public Relatorio(Pesquisa pesquisa, String titulo, Estatisticas[] est){
       this.pesquisa=pesquisa;
       this.titulo=titulo;
       this.estatisticas=Arrays.stream(est).toList();
   }

    public List<Estatisticas> getEstatisticas() {
        return estatisticas;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public String getTitulo() {
        return titulo;
    }
}
