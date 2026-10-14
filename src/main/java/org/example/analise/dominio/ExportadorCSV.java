package org.example.analise.dominio;

import org.example.comum.RespostaEspecial;

// Gera o relatorio em CSV (separado por ponto e virgula, como o Excel em portugues espera)
public class ExportadorCSV implements Exportador {

    @Override
    public String exportar(Relatorio relatorio) {
        ResultadoAnalise r = relatorio.getResultado();
        StringBuilder csv = new StringBuilder();
        csv.append("Opção;Quantidade;Percentual\n");
        for (ItemResultado item : r.getItens()) {
            csv.append(item.getRotulo()).append(';')
               .append(item.getQuantidade()).append(';')
               .append(item.getPercentual()).append('\n');
        }
        // RN-ANA-2 e RN-ANA-4: especiais separados e totais usados no calculo
        for (RespostaEspecial tipo : RespostaEspecial.values()) {
            csv.append(tipo.getDescricao()).append(';').append(r.getQuantidadeEspecial(tipo)).append(";-\n");
        }
        csv.append("Total de respostas válidas;").append(r.getTotalValidas()).append(";100.0\n");
        return csv.toString();
    }

    @Override
    public String getExtensao() {
        return "csv";
    }
}
