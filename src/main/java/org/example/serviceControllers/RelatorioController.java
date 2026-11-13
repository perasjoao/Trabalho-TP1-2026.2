package org.example.serviceControllers;

import org.example.exceptions.PesquisaInvalidaException;
import org.example.exceptions.RelatorioException;
import org.example.models.ComparaPesquisa;
import org.example.models.Entrevista;
import org.example.models.Estatisticas;
import org.example.models.Relatorio;
import org.example.models.coleta.Resposta;
import org.example.models.researchModels.Pesquisa;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class RelatorioController {
    private List<Relatorio>relatorios;
    private final String pastaRelatorios = "dados/relatorios/";

    public RelatorioController() {
        this.relatorios = new ArrayList<>();
        File pasta = new File(pastaRelatorios);
        if (!pasta.exists()) {
            pasta.mkdirs(); // so cria uma pasta se ela n ja existir para exportar os relatorios para la
        }
    }

    public Relatorio gerarRelatorio(Pesquisa pesquisa, String titulo, List<Entrevista> entrevistas, String regiao, LocalDate dataInicio, LocalDate dataFim, String candidato)
            throws RelatorioException, PesquisaInvalidaException, IOException {
        if (pesquisa == null) {
            throw new PesquisaInvalidaException("A pesquisa precisa ser informada.");
        }
        if (entrevistas == null || entrevistas.isEmpty()) {
            throw new RelatorioException("A lista de entrevistas precisa ser informada e não pode estar vazia.");
        }

        List<Entrevista> entrevistasFiltradas = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            // Filtro por Região
            // if (regiao != null && !regiao.trim().isEmpty()) {
            //if (e.getRegiao() == null || !e.getRegiao().equalsIgnoreCase(regiao.trim())) {
            //continue; // Pula se não for da região
            //}
            //}

            // Filtro por Data Inicial
            // if (dataInicio != null && e.getData() != null) {
            //if (e.getData().isBefore(dataInicio)) {
            //continue; // Pula se anterior à data inicial
            //}
            //}

            // Filtro por Data Final
            //if (dataFim != null && e.getData() != null) {
            //if (e.getData().isAfter(dataFim)) {
            //continue; // Pula se posterior à data final
            //}
            //}

            //filtrar por candidato
            if (candidato != null && !candidato.trim().isEmpty()) {
                boolean votouNoCandidato = false;
                if (e.getRespostas() != null) {
                    for (Resposta r : e.getRespostas()) {
                        if (r.getOpcaoEscolhida() != null && r.getOpcaoEscolhida().equalsIgnoreCase(candidato.trim())) {
                            votouNoCandidato = true;
                            break;
                        }
                    }
                }
                if (!votouNoCandidato) {
                    continue; //se n votou no candidato pula
                }
            }
            // Se passou por todos os filtros, adiciona na lista
            entrevistasFiltradas.add(e);
        }
        if (entrevistasFiltradas.isEmpty()) {
            throw new RelatorioException("Nenhuma entrevista encontrada para os filtros selecionados.");
        }

        List<Estatisticas> listaEstatisticas = new ArrayList<>();

        //estatisticas gerais
        List<Estatisticas> estatisticasGerais = processarEstatisticas(entrevistasFiltradas, "Geral");
        for (Estatisticas est : estatisticasGerais) {
            listaEstatisticas.add(est);
        }

        //separa as estatisticas por gruos
        Map<String, List<Entrevista>> gruposPorGenero = new HashMap<>();
        Map<String, List<Entrevista>> gruposPorEscolaridade = new HashMap<>();
        Map<String, List<Entrevista>> gruposPorFaixaEtaria = new HashMap<>();

        for (Entrevista e : entrevistasFiltradas) {
            //agrupa por genero
            if (e.getParticipante() != null) {
                String genero = e.getParticipante().getGenero();
                if (genero != null && !genero.trim().isEmpty()) {
                    if (!gruposPorGenero.containsKey(genero)) {
                        gruposPorGenero.put(genero, new ArrayList<>());
                    }
                    gruposPorGenero.get(genero).add(e);
                }
                //agrupa por escolaridade
                String escolaridade = e.getParticipante().getEscolaridade();
                if (escolaridade != null && !escolaridade.trim().isEmpty()) {
                    if (!gruposPorEscolaridade.containsKey(escolaridade)) {
                        gruposPorEscolaridade.put(escolaridade, new ArrayList<>());
                    }
                    gruposPorEscolaridade.get(escolaridade).add(e);
                }
                //agrupa por faixa etaria
                String faixaEtaria = e.getParticipante().getFaixaEtaria();
                if (faixaEtaria != null && !faixaEtaria.trim().isEmpty()) {
                    if (!gruposPorFaixaEtaria.containsKey(faixaEtaria)) {
                        gruposPorFaixaEtaria.put(faixaEtaria, new ArrayList<>());
                    }
                    gruposPorFaixaEtaria.get(faixaEtaria).add(e);
                }
            }
        }

        // estatisticas por gênero
        for (String genero : gruposPorGenero.keySet()) {
            List<Entrevista> entrevistasDoGenero = gruposPorGenero.get(genero);
            List<Estatisticas> statsGenero = processarEstatisticas(entrevistasDoGenero, "Gênero: " + genero);
            for (Estatisticas est : statsGenero) {
                listaEstatisticas.add(est);
            }
        }

        //estatisticas por escolaridade
        for (String escolaridade : gruposPorEscolaridade.keySet()) {
            List<Entrevista> entrevistasDaEscolaridade = gruposPorEscolaridade.get(escolaridade);
            List<Estatisticas> statsEscolaridade = processarEstatisticas(entrevistasDaEscolaridade, "Escolaridade: " + escolaridade);
            for (Estatisticas est : statsEscolaridade) {
                listaEstatisticas.add(est);
            }
        }

        //estatisticas por faixa etaria
        for (String faixa : gruposPorFaixaEtaria.keySet()) {
            List<Entrevista> entrevistasDaFaixa = gruposPorFaixaEtaria.get(faixa);
            List<Estatisticas> statsFaixa = processarEstatisticas(entrevistasDaFaixa, "Faixa Etária: " + faixa);
            for (Estatisticas est : statsFaixa) {
                listaEstatisticas.add(est);
            }
        }

        Relatorio relatorio = new Relatorio(pesquisa, titulo, listaEstatisticas.toArray(new Estatisticas[0]));
        this.salvarRelatorio(relatorio);
        this.exportaRelatorio(relatorio, "relatorio_" + pesquisa.getCodigo() + ".csv");

        return relatorio;
    }

    private List<Estatisticas> processarEstatisticas(List<Entrevista> listaDeEntrevistas, String rotuloSegmento) {
        Map<String, Map<String, Integer>> contagemPorPergunta = new HashMap<>();

        //vai em cada entrevista e vai em cada resposta contar a reposta para a pergunta
        for (Entrevista entrevista : listaDeEntrevistas) {
            if (entrevista.getRespostas() == null) {
                continue;
            }
            for (Resposta resposta : entrevista.getRespostas()) {
                String pergunta = resposta.getTextoPergunta();
                String opcao = resposta.getOpcaoEscolhida();
                if (!contagemPorPergunta.containsKey(pergunta)) {
                    contagemPorPergunta.put(pergunta, new HashMap<>());
                }
                Map<String, Integer> mapaOpcoes = contagemPorPergunta.get(pergunta);
                if (mapaOpcoes.containsKey(opcao)) {
                    mapaOpcoes.put(opcao, mapaOpcoes.get(opcao) + 1);
                } else {
                    mapaOpcoes.put(opcao, 1);
                }
            }
        }
        List<Estatisticas> resultadoSegmentado = new ArrayList<>();

        for (String textoPergunta : contagemPorPergunta.keySet()) {
            Map<String, Integer> mapaOpcoes = contagemPorPergunta.get(textoPergunta);
            int totalValidos = 0;
            int totalVotos = 0;

            for (String opcao : mapaOpcoes.keySet()) {
                int qtd = mapaOpcoes.get(opcao);
                totalVotos += qtd;
                if (validaVoto(opcao)) {
                    totalValidos += qtd;
                }
            }

            Map<String, Float> percentuais = new LinkedHashMap<>();
            for (String opcao : mapaOpcoes.keySet()) {
                int qtd = mapaOpcoes.get(opcao);
                float pct = 0.0f;

                String chaveComQuantidade;

                if (validaVoto(opcao)) {
                    if (totalValidos > 0) {
                        pct = ((float) qtd / totalValidos) * 100.0f;
                    }
                    chaveComQuantidade = opcao + " (" + qtd + " votos)";
                    percentuais.put(chaveComQuantidade, Math.round(pct * 100.0f) / 100.0f);
                    //bota o total absoluto direto no nome
                } else {
                    if (totalVotos > 0) {
                        pct = ((float) qtd / totalVotos) * 100.0f;
                    }
                    chaveComQuantidade = opcao + " (Abstenção/Indeciso - " + qtd + " votos)";
                    percentuais.put(chaveComQuantidade, Math.round(pct * 100.0f) / 100.0f);
                }
            }
            String parametroFormatado = "[" + rotuloSegmento + "] " + textoPergunta;
            resultadoSegmentado.add(new Estatisticas(parametroFormatado, percentuais));
        }
        return resultadoSegmentado;
    }

    public ComparaPesquisa compararPesquisas(Relatorio r1, Relatorio r2) throws RelatorioException {
        if (r1 == null || r2 == null) {
            throw new RelatorioException("Precisa de dois relatorios para fazer a comparação");
        }
        String cargo1 = r1.getPesquisa().getCargo();
        String cargo2 = r2.getPesquisa().getCargo();
        if (!cargo1.equalsIgnoreCase(cargo2)) {
            throw new RelatorioException("Pesquisas de cargos diferentes");
        }

        List<Estatisticas> listaComparacoes = new ArrayList<>();
        if (r1.getEstatisticas() == null || r2.getEstatisticas() == null) {
            return new ComparaPesquisa(r1, r2, listaComparacoes);
        }

        for (Estatisticas est1 : r1.getEstatisticas()) {
            String parametroR1 = est1.getParametro();
            for (Estatisticas est2 : r2.getEstatisticas()) {
                String parametroR2 = est2.getParametro();
                if (parametroR1.equalsIgnoreCase(parametroR2)) {
                    Map<String, Float> mapaOpcoesR1 = est1.getEstatistica();
                    Map<String, Float> mapaOpcoesR2 = est2.getEstatistica();
                    Map<String, Float> variacoes = new LinkedHashMap<>();
                    for (String opcaoCompletaR1 : mapaOpcoesR1.keySet()) {
                        float pctR1 = mapaOpcoesR1.get(opcaoCompletaR1);
                        float pctR2 = 0.0f;
                        String nomeLimpoR1 = opcaoCompletaR1.split(" \\(")[0];
                        //limpa os textos para tirar o voto absoluto
                        for (String opcaoCompletaR2 : mapaOpcoesR2.keySet()) {
                            String nomeLimpoR2 = opcaoCompletaR2.split(" \\(")[0];
                            if (nomeLimpoR1.equalsIgnoreCase(nomeLimpoR2)) {
                                pctR2 = mapaOpcoesR2.get(opcaoCompletaR2);
                                break;
                            }
                        }
                        float diferenca = pctR2 - pctR1;
                        //guarda sem o voo absoluto
                        variacoes.put(nomeLimpoR1, Math.round(diferenca * 100.0f) / 100.0f);
                    }
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
        return Collections.unmodifiableList(this.relatorios);
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
                    writer.write("Opção;Percentual\n");

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

    private boolean validaVoto(String opcao) {
        if (opcao == null) return false;
        String normalizada = opcao.trim().toLowerCase();
        return !(normalizada.equals("não sabe") ||
                normalizada.equals("nao sabe") ||
                normalizada.equals("não respondeu") ||
                normalizada.equals("nao respondeu") ||
                normalizada.equals("nenhuma das opções") ||
                normalizada.equals("branco") ||
                normalizada.equals("nulo"));
    }
}