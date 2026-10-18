package org.example.models;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Relatorio {
   private List<Estatisticas> estatisticas;
   private Entrevista entrevista;
   private String titulo;

   public Relatorio(Entrevista pesquisa,String titulo, Estatisticas[] est){
       this.entrevista=pesquisa;
       this.titulo=titulo;
       this.estatisticas=Arrays.stream(est).toList();
   }

    public List<Estatisticas> getEstatisticas() {
        return estatisticas;
    }

    public Entrevista getEntrevista() {
        return entrevista;
    }

    public String getTitulo() {
        return titulo;
    }
}
