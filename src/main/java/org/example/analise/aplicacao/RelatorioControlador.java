package org.example.analise.aplicacao;

import org.example.analise.dominio.CriterioAgrupamento;
import org.example.analise.dominio.Exportador;
import org.example.analise.dominio.ExportadorFactory;
import org.example.analise.dominio.FiltroRelatorio;
import org.example.analise.dominio.FormatoExportacao;
import org.example.analise.dominio.ItemResultado;
import org.example.analise.dominio.Relatorio;
import org.example.analise.dominio.ResultadoAnalise;
import org.example.coleta.dominio.Entrevista;
import org.example.pesquisas.dominio.Pergunta;

import java.util.ArrayList;
import java.util.List;

// Casos de uso "Gerar relatorio" e "Exportar relatorio" (RF-ANA-4, 6 e 7)
public class RelatorioControlador {

    private final ProcessamentoControlador processamento;

    public RelatorioControlador(ProcessamentoControlador processamento) {
        this.processamento = processamento;
    }

    public Relatorio gerar(FiltroRelatorio filtro, Pergunta pergunta, List<Entrevista> entrevistas,
                           CriterioAgrupamento criterio) {
        // aplica os filtros de periodo, regiao e caracteristicas da amostra
        List<Entrevista> filtradas = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            if (filtro.aceita(e)) {
                filtradas.add(e);
            }
        }
        ResultadoAnalise resultado = processamento.calcularPorCriterio(filtro.getPesquisa(), pergunta, filtradas, criterio);

        // filtro de candidato: mantem so a linha dele (os totais continuam os mesmos)
        if (filtro.getCandidato() != null) {
            List<ItemResultado> doCandidato = new ArrayList<>();
            for (ItemResultado item : resultado.getItens()) {
                if (item.getRotulo().startsWith(filtro.getCandidato())) {
                    doCandidato.add(item);
                }
            }
            resultado = new ResultadoAnalise(resultado.getPesquisa(), resultado.getPergunta(), resultado.getCriterio(),
                    doCandidato, resultado.getTotalValidas(), resultado.getEspeciais());
        }

        String titulo = "Relatório - " + filtro.getPesquisa().getNome();
        return new Relatorio(titulo, filtro, resultado);
    }

    // a escolha do formato fica com a Factory; aqui so usamos a interface Exportador
    public String exportar(Relatorio relatorio, FormatoExportacao formato) {
        Exportador exportador = ExportadorFactory.criar(formato);
        return exportador.exportar(relatorio);
    }
}
