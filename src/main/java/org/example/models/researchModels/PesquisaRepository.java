package org.example.models.researchModels;

import org.example.exceptions.PersistenciaException;
import org.example.models.comum.Cargo;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PesquisaRepository {
    private final String CAMINHO_ARQUIVO = "pesquisas_data.txt";

    public void salvarTodas(List<Pesquisa> pesquisas) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            for (Pesquisa p : pesquisas) {
                String cargoStr = (p.getCargo() != null) ? p.getCargo().name() : "";
                String dataInicioStr = (p.getDataInicio() != null) ? p.getDataInicio().toString() : "";
                String dataFimStr = (p.getDataFim() != null) ? p.getDataFim().toString() : "";
                String metodologiaLimpa = p.getMetodologia().replace("\n", " ").replace(";", ",");
                String abrangenciaLimpa = (p.getAbrangencia() != null) ? p.getAbrangencia().replace(";", ",") : "";

                // Regiões
                String regioesStr = p.getRegioesAssociadas().stream()
                        .map(r -> r.replace(";", " ").replace(",", " ").trim())
                        .filter(r -> !r.isEmpty())
                        .collect(Collectors.joining(","));

                // Números de candidaturas
                String candidaturasStr;
                if (!p.getCandidaturasAssociadas().isEmpty()) {
                    candidaturasStr = p.getCandidaturasAssociadas().stream()
                            .map(c -> String.valueOf(c.getNumero()))
                            .collect(Collectors.joining(","));
                } else {
                    candidaturasStr = p.getNumerosCandidaturasSalvas().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                }

                // Perguntas
                String perguntasStr = serializarPerguntas(p.getPerguntas());

                // Metas do Plano Amostral
                String planoAmostralStr = serializarPlanoAmostral(p.getPlanoAmostral());

                bw.write(String.join(";",
                        p.getCodigo(),
                        p.getNome(),
                        cargoStr,
                        p.getTipo(),
                        p.getSituacao(),
                        abrangenciaLimpa,
                        dataInicioStr,
                        dataFimStr,
                        String.valueOf(p.getEntrevistasPrevistas()),
                        metodologiaLimpa,
                        String.valueOf(p.getEntrevistasRealizadas()),
                        regioesStr,
                        candidaturasStr,
                        perguntasStr,
                        planoAmostralStr
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar dados no arquivo.", e);
        }
    }

    public List<Pesquisa> carregarTodas() throws PersistenciaException {
        List<Pesquisa> pesquisas = new ArrayList<>();
        File file = new File(CAMINHO_ARQUIVO);

        if (!file.exists()) {
            return pesquisas;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                Pesquisa p = converterLinhaParaPesquisa(linha);
                if (p != null) {
                    pesquisas.add(p);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao carregar dados do arquivo.", e);
        }
        return pesquisas;
    }

    private Pesquisa converterLinhaParaPesquisa(String linha) {
        String[] partes = linha.split(";", -1);
        if (partes.length >= 10) {
            String codigo = partes[0];
            String nome = partes[1];
            Cargo cargo = !partes[2].isEmpty() ? Cargo.valueOf(partes[2]) : null;
            String tipo = partes[3];
            String situacao = partes[4];
            String abrangencia = partes[5];
            LocalDate dataInicio = !partes[6].isEmpty() ? LocalDate.parse(partes[6]) : null;
            LocalDate dataFim = !partes[7].isEmpty() ? LocalDate.parse(partes[7]) : null;
            int entrevistasPrevistas = Integer.parseInt(partes[8]);
            String metodologia = partes[9];

            List<Pergunta> perguntas = (partes.length >= 14) ? desserializarPerguntas(partes[13]) : new ArrayList<>();

            Pesquisa p = new Pesquisa(codigo, nome, cargo, tipo, situacao, abrangencia,
                    dataInicio, dataFim, entrevistasPrevistas, metodologia, perguntas);

            if (partes.length >= 11 && !partes[10].isEmpty()) {
                p.setEntrevistasRealizadas(Integer.parseInt(partes[10]));
            }

            if (partes.length >= 12 && !partes[11].isEmpty()) {
                List<String> regioes = new ArrayList<>();
                for (String reg : partes[11].split(",")) {
                    String regNormalizada = normalizarRegiaoLegada(reg);
                    if (!regNormalizada.isEmpty() && !regioes.contains(regNormalizada)) {
                        regioes.add(regNormalizada);
                    }
                }
                p.setRegioesAssociadas(regioes);
            }

            if (partes.length >= 13 && !partes[12].isEmpty()) {
                List<Integer> numeros = new ArrayList<>();
                for (String num : partes[12].split(",")) {
                    if (!num.isEmpty()) numeros.add(Integer.parseInt(num));
                }
                p.setNumerosCandidaturasSalvas(numeros);
            }

            if (partes.length >= 15 && !partes[14].isEmpty()) {
                desserializarPlanoAmostral(partes[14], p.getPlanoAmostral());
            }

            return p;
        }
        return null;
    }

    private String normalizarRegiaoLegada(String reg) {
        if (reg == null) return "";
        switch (reg.trim().toUpperCase()) {
            case "NORTE": return "Norte";
            case "NORDESTE": return "Nordeste";
            case "CENTRO_OESTE": return "Centro-Oeste";
            case "SUDESTE": return "Sudeste";
            case "SUL": return "Sul";
            case "NACIONAL": return "Todas";
            default: return reg.trim();
        }
    }

    private String serializarPlanoAmostral(PlanoAmostral plano) {
        if (plano == null || plano.getMetasPorCategoria().isEmpty()) return "";
        List<String> blocos = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> catEntry : plano.getMetasPorCategoria().entrySet()) {
            String catLimpa = catEntry.getKey().replace("~", " ").replace("|", " ").replace(";", " ");
            for (Map.Entry<String, Integer> grpEntry : catEntry.getValue().entrySet()) {
                String grpLimpo = grpEntry.getKey().replace("~", " ").replace("|", " ").replace(";", " ");
                blocos.add(catLimpa + "~" + grpLimpo + "~" + grpEntry.getValue());
            }
        }
        return String.join("|", blocos);
    }

    private void desserializarPlanoAmostral(String texto, PlanoAmostral plano) {
        if (texto == null || texto.isEmpty() || plano == null) return;
        for (String bloco : texto.split("\\|")) {
            String[] campos = bloco.split("~", -1);
            if (campos.length == 3) {
                plano.definirMeta(campos[0], campos[1], Integer.parseInt(campos[2]));
            }
        }
    }

    private String serializarPerguntas(List<Pergunta> perguntas) {
        if (perguntas == null || perguntas.isEmpty()) return "";
        List<String> blocos = new ArrayList<>();
        for (Pergunta q : perguntas) {
            String opcoesStr = q.getOpcoes().stream()
                    .map(o -> o.getNumero() + ":" + o.getTexto().replace(":", " ").replace(",", " "))
                    .collect(Collectors.joining(","));
            String enunciadoLimpo = q.getEnunciado().replace("~", " ").replace("|", " ").replace(";", " ");
            String todosFlag = q.isParaTodosCandidatos() ? "1" : "0";

            String candsStr;
            if (!q.getCandidaturasAssociadas().isEmpty()) {
                candsStr = q.getCandidaturasAssociadas().stream()
                        .map(c -> String.valueOf(c.getNumero()))
                        .collect(Collectors.joining(","));
            } else {
                candsStr = q.getNumerosCandidaturasSalvas().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
            }

            blocos.add(q.getId() + "~" + enunciadoLimpo + "~" + q.getTipo() + "~" + opcoesStr + "~" + todosFlag + "~" + candsStr);
        }
        return String.join("|", blocos);
    }

    private List<Pergunta> desserializarPerguntas(String texto) {
        List<Pergunta> lista = new ArrayList<>();
        if (texto == null || texto.isEmpty()) return lista;

        for (String bloco : texto.split("\\|")) {
            String[] campos = bloco.split("~", -1);
            if (campos.length >= 3) {
                List<OpcaoResposta> opcoes = new ArrayList<>();
                if (campos.length >= 4 && !campos[3].isEmpty()) {
                    for (String op : campos[3].split(",")) {
                        String[] par = op.split(":", 2);
                        if (par.length == 2) {
                            opcoes.add(new OpcaoResposta(Integer.parseInt(par[0]), par[1]));
                        }
                    }
                }
                Pergunta q = new Pergunta(campos[0], campos[1], campos[2], opcoes);

                if (campos.length >= 5 && "1".equals(campos[4])) {
                    q.setParaTodosCandidatos(true);
                }

                if (campos.length >= 6 && !campos[5].isEmpty()) {
                    List<Integer> nums = new ArrayList<>();
                    for (String n : campos[5].split(",")) {
                        if (!n.isEmpty()) nums.add(Integer.parseInt(n));
                    }
                    q.setNumerosCandidaturasSalvas(nums);
                }

                lista.add(q);
            }
        }
        return lista;
    }
}