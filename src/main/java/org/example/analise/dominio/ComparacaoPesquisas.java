package org.example.analise.dominio;

import org.example.pesquisas.dominio.Pesquisa;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Comparacao entre os resultados de duas pesquisas (RF-ANA-3)
public class ComparacaoPesquisas {

    private final Pesquisa pesquisaA;
    private final Pesquisa pesquisaB;
    private final List<ItemComparacao> itens = new ArrayList<>();

    public ComparacaoPesquisas(ResultadoAnalise resultadoA, ResultadoAnalise resultadoB) {
        this.pesquisaA = resultadoA.getPesquisa();
        this.pesquisaB = resultadoB.getPesquisa();
        if (!saoCompativeis(pesquisaA, pesquisaB)) {
            throw new IllegalArgumentException("As pesquisas " + pesquisaA.getCodigo() + " e " + pesquisaB.getCodigo()
                    + " não são compatíveis: é preciso o mesmo cargo e o mesmo tipo de pesquisa.");
        }
        // todos os rotulos que aparecem em pelo menos uma das pesquisas
        Set<String> rotulos = new LinkedHashSet<>();
        resultadoA.getItens().forEach(i -> rotulos.add(i.getRotulo()));
        resultadoB.getItens().forEach(i -> rotulos.add(i.getRotulo()));
        for (String rotulo : rotulos) {
            itens.add(new ItemComparacao(rotulo, resultadoA.getPercentual(rotulo), resultadoB.getPercentual(rotulo)));
        }
    }

    // RN-ANA-5: criterio de compatibilidade definido pelo sistema = mesmo cargo e mesmo tipo
    public static boolean saoCompativeis(Pesquisa a, Pesquisa b) {
        return a != b && a.getCargo() == b.getCargo() && a.getTipo() == b.getTipo();
    }

    public Pesquisa getPesquisaA() {
        return pesquisaA;
    }

    public Pesquisa getPesquisaB() {
        return pesquisaB;
    }

    public List<ItemComparacao> getItens() {
        return List.copyOf(itens);
    }
}
