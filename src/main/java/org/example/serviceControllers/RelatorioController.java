package org.example.serviceControllers;

import org.example.models.Candidato;
import org.example.models.Pesquisa;
import org.example.models.Relatorio;

import java.util.Map;

public class RelatorioController {

    public Relatorio gerarRelatorio(Candidato candidato,Map<String,Float> estatisticas){}

    void comparaPesquisas(Relatorio rel1,Relatorio rel2){}

    public boolean exportarRelatorio(){//retorna se deu certo a exportacao
    }

    public Relatorio[] filtrarRelatorio(Map<String,String>){} //<Parametro,Valor>

    Map<Candidato, Relatorio>[] extraiEstatisticas(Candidato candidato, String[]parametros){}
}
