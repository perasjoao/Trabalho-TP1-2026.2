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
        salvarRelatorio(relatorio);
        return relatorio;
    }
    public ComparaPesquisa compararPesquisas(Relatorio r1, Relatorio r2, Estatisticas[] est) throws RelatorioException {
        if (r1 == null || r2 == null) {
            throw new RelatorioException("Ambos os relatórios devem ser fornecidos para comparação.");
        }
        if (!r1.getPesquisa().getCargo().equalsIgnoreCase(r2.getPesquisa().getCargo())) {
            throw new RelatorioException("Pesquisas incompatíveis para comparação: cargos distintos ("
                    + r1.getPesquisa().getCargo() + " != " + r2.getPesquisa().getCargo() + ").");
        }

        return new ComparaPesquisa(r1, r2, est != null ? est : new Estatisticas[0]);
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