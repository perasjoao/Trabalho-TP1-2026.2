package org.example.analise.aplicacao;

import org.example.analise.dominio.AgrupamentoPorOpcao;
import org.example.analise.dominio.CalculadoraEstatistica;
import org.example.analise.dominio.CriterioAgrupamento;
import org.example.analise.dominio.ResultadoAnalise;
import org.example.coleta.dominio.Entrevista;
import org.example.comum.SituacaoPesquisa;
import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Casos de uso "Processar pesquisa" e "Consultar resultados estatisticos" (RF-ANA-1, 2 e 5)
public class ProcessamentoControlador {

    private final CalculadoraEstatistica calculadora = new CalculadoraEstatistica();
    private final Map<Pesquisa, List<ResultadoAnalise>> resultados = new HashMap<>();

    // RN-PES-8: so pesquisas encerradas sao processadas.
    // Gera o resultado de cada pergunta por opcao escolhida e marca a pesquisa como processada.
    public List<ResultadoAnalise> processar(Pesquisa pesquisa, List<Entrevista> entrevistas) {
        if (pesquisa.getSituacao() != SituacaoPesquisa.ENCERRADA && pesquisa.getSituacao() != SituacaoPesquisa.PROCESSADA) {
            throw new IllegalStateException("A pesquisa " + pesquisa.getCodigo()
                    + " só pode ser processada após o encerramento da coleta.");
        }
        List<Entrevista> validas = somenteConcluidas(entrevistas);
        List<ResultadoAnalise> daPesquisa = new ArrayList<>();
        for (Pergunta pergunta : pesquisa.getPerguntas()) {
            daPesquisa.add(calculadora.calcular(pesquisa, pergunta, validas, new AgrupamentoPorOpcao()));
        }
        pesquisa.marcarProcessada();
        resultados.put(pesquisa, daPesquisa);
        return daPesquisa;
    }

    public List<ResultadoAnalise> getResultados(Pesquisa pesquisa) {
        exigirProcessada(pesquisa);
        return new ArrayList<>(resultados.get(pesquisa));
    }

    // a primeira pergunta e a principal (ex.: intencao de voto); usada nas comparacoes
    public ResultadoAnalise getResultadoPrincipal(Pesquisa pesquisa) {
        List<ResultadoAnalise> daPesquisa = getResultados(pesquisa);
        if (daPesquisa.isEmpty()) {
            throw new IllegalStateException("A pesquisa " + pesquisa.getCodigo() + " não tem perguntas.");
        }
        return daPesquisa.get(0);
    }

    // RF-ANA-1 e 2: o mesmo calculo com outro criterio (regiao, faixa etaria, conjunto...)
    public ResultadoAnalise calcularPorCriterio(Pesquisa pesquisa, Pergunta pergunta, List<Entrevista> entrevistas,
                                                CriterioAgrupamento criterio) {
        exigirProcessada(pesquisa);
        return calculadora.calcular(pesquisa, pergunta, somenteConcluidas(entrevistas), criterio);
    }

    // RN-PES-9: so pesquisas processadas sao consultadas
    private void exigirProcessada(Pesquisa pesquisa) {
        if (!resultados.containsKey(pesquisa)) {
            throw new IllegalStateException("A pesquisa " + pesquisa.getCodigo() + " ainda não foi processada.");
        }
    }

    private static List<Entrevista> somenteConcluidas(List<Entrevista> entrevistas) {
        List<Entrevista> concluidas = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            if (e.isConcluida()) {
                concluidas.add(e);
            }
        }
        return concluidas;
    }
}
