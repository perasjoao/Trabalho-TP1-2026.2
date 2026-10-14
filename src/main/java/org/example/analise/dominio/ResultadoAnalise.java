package org.example.analise.dominio;

import org.example.comum.RespostaEspecial;
import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Resultado do calculo de uma pergunta segundo um criterio.
// As respostas especiais ficam separadas dos itens (RN-ANA-2).
public class ResultadoAnalise {

    private final Pesquisa pesquisa;
    private final Pergunta pergunta;
    private final String criterio;
    private final List<ItemResultado> itens;
    private final int totalValidas;
    private final Map<RespostaEspecial, Integer> especiais;

    public ResultadoAnalise(Pesquisa pesquisa, Pergunta pergunta, String criterio, List<ItemResultado> itens,
                            int totalValidas, Map<RespostaEspecial, Integer> especiais) {
        this.pesquisa = pesquisa;
        this.pergunta = pergunta;
        this.criterio = criterio;
        this.itens = List.copyOf(itens);
        this.totalValidas = totalValidas;
        this.especiais = new EnumMap<>(especiais);
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public String getCriterio() {
        return criterio;
    }

    public List<ItemResultado> getItens() {
        return itens;
    }

    // base dos percentuais (RN-ANA-1)
    public int getTotalValidas() {
        return totalValidas;
    }

    public int getQuantidadeEspecial(RespostaEspecial tipo) {
        return especiais.getOrDefault(tipo, 0);
    }

    public Map<RespostaEspecial, Integer> getEspeciais() {
        return Collections.unmodifiableMap(especiais);
    }

    public int getTotalRespostas() {
        int total = totalValidas;
        for (int quantidade : especiais.values()) {
            total += quantidade;
        }
        return total;
    }

    // percentual de um rotulo, ou 0 se ele nao apareceu
    public double getPercentual(String rotulo) {
        for (ItemResultado item : itens) {
            if (item.getRotulo().equals(rotulo)) {
                return item.getPercentual();
            }
        }
        return 0;
    }
}
