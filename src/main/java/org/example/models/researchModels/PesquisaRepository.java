package org.example.models.researchModels;

import org.example.exceptions.PersistenciaException;
import org.example.models.candidatos.Candidatura;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

                // Regiões separadas por vírgula
                String regioesStr = p.getRegioesAssociadas().stream()
                        .map(Regiao::name)
                        .collect(Collectors.joining(","));

                // Números das candidaturas separados por vírgula
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

                // Perguntas serializadas
                String perguntasStr = serializarPerguntas(p.getPerguntas());

                bw.write(String.join(";",
                        p.getCodigo(),
                        p.getNome(),
                        cargoStr,
                        p.getTipo(),
                        p.getSituacao(),
                        p.getAbrangencia(),
                        dataInicioStr,
                        dataFimStr,
                        String.valueOf(p.getEntrevistasPrevistas()),
                        metodologiaLimpa,
                        String.valueOf(p.getEntrevistasRealizadas()),
                        regioesStr,
                        candidaturasStr,
                        perguntasStr
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
        String[] partes = linha.split(";", -1); // -1 mantém campos vazios no final
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
                List<Regiao> regioes = new ArrayList<>();
                for (String reg : partes[11].split(",")) {
                    if (!reg.isEmpty()) regioes.add(Regiao.valueOf(reg));
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

            return p;
        }
        return null;
    }

    private String serializarPerguntas(List<Pergunta> perguntas) {
        if (perguntas == null || perguntas.isEmpty()) return "";
        List<String> blocos = new ArrayList<>();
        for (Pergunta q : perguntas) {
            String opcoesStr = q.getOpcoes().stream()
                    .map(o -> o.getNumero() + ":" + o.getTexto().replace(":", " ").replace(",", " "))
                    .collect(Collectors.joining(","));
            String enunciadoLimpo = q.getEnunciado().replace("~", " ").replace("|", " ").replace(";", " ");
            blocos.add(q.getId() + "~" + enunciadoLimpo + "~" + q.getTipo() + "~" + opcoesStr);
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
                lista.add(new Pergunta(campos[0], campos[1], campos[2], opcoes));
            }
        }
        return lista;
    }
}