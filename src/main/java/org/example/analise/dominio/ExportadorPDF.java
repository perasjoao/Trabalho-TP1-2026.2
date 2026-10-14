package org.example.analise.dominio;

import org.example.comum.RespostaEspecial;

// Monta o conteudo do relatorio no layout do PDF. Nesta etapa o resultado e texto;
// a gravacao do arquivo .pdf entra na etapa 3, junto com a persistencia.
public class ExportadorPDF implements Exportador {

    @Override
    public String exportar(Relatorio relatorio) {
        ResultadoAnalise r = relatorio.getResultado();
        StringBuilder texto = new StringBuilder();
        texto.append(relatorio.getTitulo()).append('\n');
        texto.append("Filtros: ").append(relatorio.getFiltro().descrever()).append('\n');
        texto.append("Pergunta: ").append(r.getPergunta().getTexto()).append('\n');
        texto.append("Agrupamento: ").append(r.getCriterio()).append('\n');
        texto.append("Gerado em: ").append(relatorio.getGeradoEm().withNano(0)).append("\n\n");
        for (ItemResultado item : r.getItens()) {
            texto.append(String.format("%-35s %6d %7.1f%%%n", item.getRotulo(), item.getQuantidade(), item.getPercentual()));
        }
        texto.append('\n');
        texto.append("Respostas válidas (base dos percentuais): ").append(r.getTotalValidas()).append('\n');
        for (RespostaEspecial tipo : RespostaEspecial.values()) {
            texto.append(tipo.getDescricao()).append(": ").append(r.getQuantidadeEspecial(tipo)).append('\n');
        }
        return texto.toString();
    }

    @Override
    public String getExtensao() {
        return "pdf";
    }
}
