package org.example.analise.aplicacao;

import org.example.analise.dominio.ComparacaoPesquisas;
import org.example.pesquisas.dominio.Pesquisa;

// Caso de uso "Comparar pesquisas" (RF-ANA-3 e RN-ANA-5)
public class ComparacaoControlador {

    private final ProcessamentoControlador processamento;

    public ComparacaoControlador(ProcessamentoControlador processamento) {
        this.processamento = processamento;
    }

    // compara a pergunta principal das duas pesquisas; ambas precisam estar processadas
    public ComparacaoPesquisas comparar(Pesquisa a, Pesquisa b) {
        return new ComparacaoPesquisas(processamento.getResultadoPrincipal(a), processamento.getResultadoPrincipal(b));
    }
}
