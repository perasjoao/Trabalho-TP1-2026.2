package org.example.serviceControllers;

import org.example.exceptions.PesquisaInvalida;
import org.example.exceptions.RelatorioException;
import org.example.models.ComparaPesquisa;
import org.example.models.Entrevista;
import org.example.models.Estatisticas;
import org.example.models.Relatorio;
import org.example.models.coleta.Resposta;
import org.example.models.researchModels.Pesquisa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RelatorioController {
    private List<Relatorio> relatorios;
    private final String pastaRelatorios = "dados/relatorios/";

    public RelatorioController() {
        this.relatorios = new ArrayList<>();
        File pasta = new File(pastaRelatorios);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }
    public Relatorio gerarRelatorio(Pesquisa pesquisa, String titulo, List<Entrevista> entrevistas) throws RelatorioException, PesquisaInvalida {

        if (pesquisa == null) {
            throw new PesquisaInvalida("A pesquisa precisa ser informada");
        }
        if (entrevistas == null || entrevistas.isEmpty()) {
            throw new RelatorioException("A entrevista precisa ser informada");
        }
        //junta a pergunta com as repostas e a quantiadade de respostas
        Map<String, Map<String, Integer>> contagemPorPergunta = new HashMap<>();
        for (Entrevista entrevista : entrevistas) {
            if (entrevista.getRespostas() == null) {
                continue;
            }
            for (Resposta resposta : entrevista.getRespostas()) {
                String pergunta = resposta.getTextoPergunta();
                String opcao = resposta.getOpcaoEscolhida();
                // ve se ja computou essa pergunta, se nao cria um has para ela
                if (!contagemPorPergunta.containsKey(pergunta)) {
                    contagemPorPergunta.put(pergunta, new HashMap<>());
                }
                // pega as respostas que ja foram computadas da pergunta
                Map<String, Integer> mapaOpcoes = contagemPorPergunta.get(pergunta);
                //att contagem
                if (mapaOpcoes.containsKey(opcao)) { //se ja tiver contando essa opcao
                    int contagemAtual = mapaOpcoes.get(opcao);
                    mapaOpcoes.put(opcao, contagemAtual + 1);
                } else {
                    mapaOpcoes.put(opcao, 1); //se nao cria ela e conta
                }
            }
        }
        List<Estatisticas> listaEstatisticas = new ArrayList<>();
        //calculo das estatisticas dos votos validos
        for (String textoPergunta : contagemPorPergunta.keySet()) {
            //pega as repostas e ve se é valido
            Map<String, Integer> mapaOpcoes = contagemPorPergunta.get(textoPergunta);
            int totalValidos = 0;
            for (String opcao : mapaOpcoes.keySet()) {
                int quantidadeVotos = mapaOpcoes.get(opcao);
                if (!ValidaVoto(opcao)) {
                    totalValidos += quantidadeVotos;
                }
            }
            Map<String, Float> percentuais = new LinkedHashMap<>(); //pra manter ordenado
            for (String opcao : mapaOpcoes.keySet()) {
                int quantidadeVotos = mapaOpcoes.get(opcao);
                if (!ValidaVoto(opcao)) { // ve se eh valido e calcula
                    float pct = 0.0f;
                    if (totalValidos > 0) {
                        pct = ((float) quantidadeVotos / totalValidos) * 100.0f;
                    }
                    //arredonda
                    float pctArredondada = Math.round(pct * 100.0f) / 100.0f;
                    percentuais.put(opcao, pctArredondada);
                } else {
                    // Se for nulo ou branco pu indeciso guarda conta nas abstençoes
                    percentuais.put(opcao + " (Abstenção/Indeciso)", (float) quantidadeVotos);
                }
            }
            //adiciona o resultado da pergunta na lista
            Estatisticas estatisticaDaPergunta = new Estatisticas(textoPergunta, percentuais);
            listaEstatisticas.add(estatisticaDaPergunta);
        }
        Relatorio relatorio = new Relatorio(pesquisa, titulo, listaEstatisticas.toArray(new Estatisticas[0]));
        this.salvarRelatorio(relatorio);
        return relatorio;
    }
    public ComparaPesquisa compararPesquisas(Relatorio r1, Relatorio r2) throws RelatorioException {
        if (r1 == null || r2 == null) {
            throw new RelatorioException("Precisa de dois relatorios para fazer a comparação");
        }
        String cargo1 = r1.getPesquisa().getCargo();
        String cargo2 = r2.getPesquisa().getCargo();
        if (!cargo1.equalsIgnoreCase(cargo2)) {
            throw new RelatorioException("Pesquisas incompatíveis");
        }
        List<Estatisticas> listaComparacoes = new ArrayList<>();
        if (r1.getEstatisticas() == null || r2.getEstatisticas() == null) {
            return new ComparaPesquisa(r1, r2, listaComparacoes);
        }
        //estatísticas do primeiro relatório
        for (Estatisticas est1 : r1.getEstatisticas()) {
            String parametroR1 = est1.getParametro();
            for (Estatisticas est2 : r2.getEstatisticas()) {
                String parametroR2 = est2.getParametro();
                if (parametroR1.equalsIgnoreCase(parametroR2)) {
                    Map<String, Float> mapaOpcoesR1 = est1.getEstatistica();
                    Map<String, Float> mapaOpcoesR2 = est2.getEstatistica();
                    Map<String, Float> variacoes = new LinkedHashMap<>();
                    for (String opcao : mapaOpcoesR1.keySet()) { //diferenca de %
                        float pctR1 = mapaOpcoesR1.get(opcao);
                        float pctR2 = 0.0f;
                        if (mapaOpcoesR2.containsKey(opcao)) {
                            pctR2 = mapaOpcoesR2.get(opcao);
                        }
                        float diferenca = pctR2 - pctR1;
                        float diferencaArredondada = Math.round(diferenca * 100.0f) / 100.0f;
                        variacoes.put(opcao, diferencaArredondada);
                    }
                    //coloca as estatisticas comparadas
                    Estatisticas estComparativa = new Estatisticas("Comparativo: " + parametroR1, variacoes);
                    listaComparacoes.add(estComparativa);
                }
            }
        }
        return new ComparaPesquisa(r1, r2, listaComparacoes);
    }

    public void salvarRelatorio(Relatorio relatorio) {
        if (relatorio != null && !relatorios.contains(relatorio)) {
            relatorios.add(relatorio);
        }
    }

    public List<Relatorio> listarRelatorios() {
        return Collections.unmodifiableList(relatorios);
    }
    public void exportaRelatorio(Relatorio relatorio, String nomeArquivo) throws IOException {
        if (relatorio == null) return;

        File arquivo = new File(pastaRelatorios + nomeArquivo);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("Título;" + relatorio.getTitulo() + "\n");
            writer.write("Pesquisa;" + relatorio.getPesquisa().getNome() + "\n");
            writer.write("Cargo;" + relatorio.getPesquisa().getCargo() + "\n\n");

            if (relatorio.getEstatisticas() != null) {
                for (Estatisticas est : relatorio.getEstatisticas()) {
                    writer.write("Parâmetro: " + est.getParametro() + "\n");
                    writer.write("Percentual\n");

                    if (est.getEstatistica() != null) {
                        for (Map.Entry<String, Float> entry : est.getEstatistica().entrySet()) {
                            writer.write(entry.getKey() + ";" + entry.getValue() + "\n");
                        }
                    }
                    writer.write("\n");
                }
            }
        }
    }

    private boolean ValidaVoto(String opcao) {
        if (opcao == null) return true;
        String normalizada = opcao.trim().toLowerCase();
        return normalizada.equals("não sabe") ||
                normalizada.equals("nao sabe") ||
                normalizada.equals("não respondeu") ||
                normalizada.equals("nao respondeu") ||
                normalizada.equals("nenhuma das opções") ||
                normalizada.equals("branco") ||
                normalizada.equals("nulo");
    }
}