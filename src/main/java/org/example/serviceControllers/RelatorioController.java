package org.example.serviceControllers;

import org.example.exceptions.PesquisaInvalidaException;
import org.example.exceptions.RelatorioException;
import org.example.models.ComparaPesquisa;
import org.example.models.Entrevista;
import org.example.models.Estatisticas;
import org.example.models.Relatorio;
import org.example.models.candidatos.Candidatura;
import org.example.models.coleta.Resposta;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;

import java.io.*;
import java.time.LocalDate;
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

    public Relatorio gerarRelatorio(Pesquisa pesquisa, String titulo, List<Entrevista> entrevistas, String regiao, LocalDate dataInicio, LocalDate dataFim, String candidato)
            throws RelatorioException, PesquisaInvalidaException, IOException {
        List<Candidatura> candidatosFiltro = null;

        if (pesquisa != null && candidato != null && !candidato.trim().isEmpty()) {
            Candidatura encontrada = resolverCandidaturaPorTexto(pesquisa, candidato.trim());
            if (encontrada != null) {
                candidatosFiltro = Collections.singletonList(encontrada);
            } else {
                // Caso a pesquisa ainda não tenha perguntas configuradas com Candidatura,
                // mantém compatibilidade buscando pelo texto da opção escolhida
                if (pesquisa.getPerguntas() == null || pesquisa.getPerguntas().isEmpty()) {
                    return gerarRelatorioLegadoPorOpcao(pesquisa, titulo, entrevistas, regiao, dataInicio, dataFim, candidato);
                }
                throw new RelatorioException("Candidato '" + candidato + "' não encontrado nesta pesquisa.");
            }
        }

        return gerarRelatorioPorCandidatos(pesquisa, titulo, entrevistas, regiao, dataInicio, dataFim, candidatosFiltro);
    }

    public Relatorio gerarRelatorioPorCandidatos(Pesquisa pesquisa, String titulo, List<Entrevista> entrevistas,
                                                 String regiao, LocalDate dataInicio, LocalDate dataFim,
                                                 List<Candidatura> candidatosSelecionados)
            throws RelatorioException, PesquisaInvalidaException, IOException {
        if (pesquisa == null) {
            throw new PesquisaInvalidaException("A pesquisa precisa ser informada.");
        }
        if (entrevistas == null || entrevistas.isEmpty()) {
            throw new RelatorioException("A lista de entrevistas precisa ser informada e não pode estar vazia.");
        }

        // Filtra as perguntas da pesquisa associadas ao(s) candidato(s) escolhido(s) ou a "Todos"
        List<Pergunta> perguntasPermitidas = null;
        if (candidatosSelecionados != null && !candidatosSelecionados.isEmpty()) {
            perguntasPermitidas = new ArrayList<>();
            if (pesquisa.getPerguntas() != null) {
                for (Pergunta q : pesquisa.getPerguntas()) {
                    boolean pertence = q.isParaTodosCandidatos() ||
                            candidatosSelecionados.stream().anyMatch(q::refereSeAoCandidato);
                    if (pertence) {
                        perguntasPermitidas.add(q);
                    }
                }
            }
            if (perguntasPermitidas.isEmpty()) {
                throw new RelatorioException("Nenhuma pergunta desta pesquisa está associada ao candidato selecionado.");
            }
        }

        List<Entrevista> entrevistasFiltradas = filtrarEntrevistasPorRegiaoEData(entrevistas, regiao, dataInicio, dataFim);
        if (entrevistasFiltradas.isEmpty()) {
            throw new RelatorioException("Nenhuma entrevista encontrada para os filtros selecionados.");
        }

        return montarEExportarRelatorio(pesquisa, titulo, entrevistasFiltradas, perguntasPermitidas);
    }

    private Relatorio gerarRelatorioLegadoPorOpcao(Pesquisa pesquisa, String titulo, List<Entrevista> entrevistas,
                                                   String regiao, LocalDate dataInicio, LocalDate dataFim, String candidato)
            throws RelatorioException, PesquisaInvalidaException, IOException {
        if (pesquisa == null) {
            throw new PesquisaInvalidaException("A pesquisa precisa ser informada.");
        }
        if (entrevistas == null || entrevistas.isEmpty()) {
            throw new RelatorioException("A lista de entrevistas precisa ser informada e não pode estar vazia.");
        }

        List<Entrevista> base = filtrarEntrevistasPorRegiaoEData(entrevistas, regiao, dataInicio, dataFim);
        List<Entrevista> entrevistasFiltradas = new ArrayList<>();
        for (Entrevista e : base) {
            boolean votouNoCandidato = false;
            if (e.getRespostas() != null) {
                for (Resposta r : e.getRespostas()) {
                    if (r.getOpcaoEscolhida() != null && r.getOpcaoEscolhida().equalsIgnoreCase(candidato.trim())) {
                        votouNoCandidato = true;
                        break;
                    }
                }
            }
            if (votouNoCandidato) {
                entrevistasFiltradas.add(e);
            }
        }

        if (entrevistasFiltradas.isEmpty()) {
            throw new RelatorioException("Nenhuma entrevista encontrada para os filtros selecionados.");
        }

        return montarEExportarRelatorio(pesquisa, titulo, entrevistasFiltradas, null);
    }

    private List<Entrevista> filtrarEntrevistasPorRegiaoEData(List<Entrevista> entrevistas, String regiao,
                                                              LocalDate dataInicio, LocalDate dataFim) {
        List<Entrevista> filtradas = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            if (regiao != null && !regiao.trim().isEmpty()) {
                if (e.getRegiao() == null || !e.getRegiao().equalsIgnoreCase(regiao.trim())) {
                    continue;
                }
            }
            if (dataInicio != null && e.getData() != null && e.getData().isBefore(dataInicio)) {
                continue;
            }
            if (dataFim != null && e.getData() != null && e.getData().isAfter(dataFim)) {
                continue;
            }
            filtradas.add(e);
        }
        return filtradas;
    }

    private Relatorio montarEExportarRelatorio(Pesquisa pesquisa, String titulo,
                                               List<Entrevista> entrevistasFiltradas,
                                               List<Pergunta> perguntasPermitidas)
            throws RelatorioException, IOException {
        List<Estatisticas> listaEstatisticas = new ArrayList<>();

        List<Estatisticas> estatisticasGerais = processarEstatisticas(entrevistasFiltradas, "Geral", perguntasPermitidas);
        if (estatisticasGerais.isEmpty()) {
            throw new RelatorioException("Nenhuma resposta encontrada para as perguntas associadas ao candidato selecionado.");
        }
        listaEstatisticas.addAll(estatisticasGerais);

        Map<String, List<Entrevista>> gruposPorGenero = new HashMap<>();
        Map<String, List<Entrevista>> gruposPorEscolaridade = new HashMap<>();
        Map<String, List<Entrevista>> gruposPorFaixaEtaria = new HashMap<>();

        for (Entrevista e : entrevistasFiltradas) {
            if (e.getParticipante() != null) {
                String genero = e.getParticipante().getGenero();
                if (genero != null && !genero.trim().isEmpty()) {
                    gruposPorGenero.computeIfAbsent(genero, k -> new ArrayList<>()).add(e);
                }
                String escolaridade = e.getParticipante().getEscolaridade();
                if (escolaridade != null && !escolaridade.trim().isEmpty()) {
                    gruposPorEscolaridade.computeIfAbsent(escolaridade, k -> new ArrayList<>()).add(e);
                }
                String faixaEtaria = e.getParticipante().getFaixaEtaria();
                if (faixaEtaria != null && !faixaEtaria.trim().isEmpty()) {
                    gruposPorFaixaEtaria.computeIfAbsent(faixaEtaria, k -> new ArrayList<>()).add(e);
                }
            }
        }

        for (String genero : gruposPorGenero.keySet()) {
            listaEstatisticas.addAll(processarEstatisticas(gruposPorGenero.get(genero), "Gênero: " + genero, perguntasPermitidas));
        }
        for (String escolaridade : gruposPorEscolaridade.keySet()) {
            listaEstatisticas.addAll(processarEstatisticas(gruposPorEscolaridade.get(escolaridade), "Escolaridade: " + escolaridade, perguntasPermitidas));
        }
        for (String faixa : gruposPorFaixaEtaria.keySet()) {
            listaEstatisticas.addAll(processarEstatisticas(gruposPorFaixaEtaria.get(faixa), "Faixa Etária: " + faixa, perguntasPermitidas));
        }

        Relatorio relatorio = new Relatorio(pesquisa, titulo, listaEstatisticas.toArray(new Estatisticas[0]));
        this.salvarRelatorio(relatorio);
        this.exportaRelatorio(relatorio, "relatorio_" + pesquisa.getCodigo() + ".csv");

        return relatorio;
    }

    private Candidatura resolverCandidaturaPorTexto(Pesquisa pesquisa, String textoCandidato) {
        if (pesquisa.getCandidaturasAssociadas() == null) return null;
        for (Candidatura c : pesquisa.getCandidaturasAssociadas()) {
            if (c == null) continue;
            String nomeComNumero = c.getCandidato().getNome() + " (" + c.getNumero() + ")";
            if (textoCandidato.equalsIgnoreCase(nomeComNumero)
                    || textoCandidato.equalsIgnoreCase(c.getDescricao())
                    || textoCandidato.equalsIgnoreCase(c.getCandidato().getNome())
                    || textoCandidato.equals(String.valueOf(c.getNumero()))) {
                return c;
            }
        }
        return null;
    }

    private boolean respostaPertenceAsPerguntas(Resposta resposta, List<Pergunta> perguntasPermitidas) {
        if (perguntasPermitidas == null) return true;
        if (resposta == null) return false;

        for (Pergunta q : perguntasPermitidas) {
            boolean bateId = resposta.getIdPergunta() != null
                    && resposta.getIdPergunta().trim().equalsIgnoreCase(q.getId().trim());
            boolean bateEnunciado = resposta.getTextoPergunta() != null
                    && resposta.getTextoPergunta().trim().equalsIgnoreCase(q.getEnunciado().trim());
            if (bateId || bateEnunciado) {
                return true;
            }
        }
        return false;
    }

    private List<Estatisticas> processarEstatisticas(List<Entrevista> listaDeEntrevistas, String rotuloSegmento,
                                                     List<Pergunta> perguntasPermitidas) {
        Map<String, Map<String, Integer>> contagemPorPergunta = new HashMap<>();

        for (Entrevista entrevista : listaDeEntrevistas) {
            if (entrevista.getRespostas() == null) {
                continue;
            }
            for (Resposta resposta : entrevista.getRespostas()) {
                if (!respostaPertenceAsPerguntas(resposta, perguntasPermitidas)) {
                    continue;
                }
                String pergunta = resposta.getTextoPergunta();
                String opcao = resposta.getOpcaoEscolhida();
                if (!contagemPorPergunta.containsKey(pergunta)) {
                    contagemPorPergunta.put(pergunta, new HashMap<>());
                }
                Map<String, Integer> mapaOpcoes = contagemPorPergunta.get(pergunta);
                mapaOpcoes.put(opcao, mapaOpcoes.getOrDefault(opcao, 0) + 1);
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
        Cargo cargo1 = r1.getPesquisa().getCargo();
        Cargo cargo2 = r2.getPesquisa().getCargo();
        if (cargo1 == null || cargo2 == null || !cargo1.getDescricao().equals(cargo2.getDescricao())) {
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
                        for (String opcaoCompletaR2 : mapaOpcoesR2.keySet()) {
                            String nomeLimpoR2 = opcaoCompletaR2.split(" \\(")[0];
                            if (nomeLimpoR1.equalsIgnoreCase(nomeLimpoR2)) {
                                pctR2 = mapaOpcoesR2.get(opcaoCompletaR2);
                                break;
                            }
                        }
                        float diferenca = pctR2 - pctR1;
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

        if (!nomeArquivo.endsWith(".csv")) {
            nomeArquivo += ".csv";
        }
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