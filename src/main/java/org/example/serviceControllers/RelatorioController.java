package org.example.serviceControllers;

import org.example.models.ComparaPesquisa;
import org.example.models.Entrevista;
import org.example.models.Estatisticas;
import org.example.models.Relatorio;

import java.util.List;
import java.util.Map;

public class RelatorioController {
    public Relatorio gerarRelatorio(Entrevista pesquisa, String titulo, Estatisticas[] est) {
        return new Relatorio(pesquisa,titulo,est);
    }

    public ComparaPesquisa compararPesquisas(Relatorio r1, Relatorio r2, Estatisticas[] est) {
        return new ComparaPesquisa(r1,r2,est);
    }

    public void salvarRelatorio(Relatorio relatorio) {
    }
    public List<Relatorio> listarRelatorios() {
        return List.of();
    }

    public void exportaRelatorio(){

    }
}
