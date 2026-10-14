package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Resposta;
import org.example.comum.RespostaEspecial;
import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Calcula quantidades e percentuais das respostas de uma pergunta.
// Recebe o criterio de agrupamento de fora (Strategy), entao o mesmo calculo
// serve para "por candidato", "por regiao" ou "candidato x faixa etaria".
public class CalculadoraEstatistica {

    // RN-ANA-3: percentuais com uma casa decimal, arredondamento "meio para cima"
    public static final int CASAS_DECIMAIS = 1;

    public ResultadoAnalise calcular(Pesquisa pesquisa, Pergunta pergunta, List<Entrevista> entrevistas,
                                     CriterioAgrupamento criterio) {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        Map<RespostaEspecial, Integer> especiais = new EnumMap<>(RespostaEspecial.class);
        int totalValidas = 0;

        for (Entrevista entrevista : entrevistas) {
            Resposta resposta = entrevista.getResposta(pergunta);
            if (resposta == null) {
                continue;
            }
            if (resposta.isValida()) {
                String grupo = criterio.classificar(entrevista, resposta);
                contagem.merge(grupo, 1, Integer::sum);
                totalValidas++;
            } else {
                // RN-ANA-2: "nao sabe" e "nao respondeu" contam a parte
                especiais.merge(resposta.getRespostaEspecial(), 1, Integer::sum);
            }
        }

        // RN-ANA-1: o percentual usa so as respostas validas
        List<ItemResultado> itens = new ArrayList<>();
        for (Map.Entry<String, Integer> grupo : contagem.entrySet()) {
            double percentual = totalValidas == 0 ? 0 : arredondar(grupo.getValue() * 100.0 / totalValidas);
            itens.add(new ItemResultado(grupo.getKey(), grupo.getValue(), percentual));
        }
        // maior quantidade primeiro
        itens.sort((a, b) -> Integer.compare(b.getQuantidade(), a.getQuantidade()));

        return new ResultadoAnalise(pesquisa, pergunta, criterio.getNome(), itens, totalValidas, especiais);
    }

    public static double arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(CASAS_DECIMAIS, RoundingMode.HALF_UP).doubleValue();
    }
}
