package org.example.models.researchModels;

import org.example.exceptions.PersistenciaException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PesquisaRepository {
    private final String CAMINHO_ARQUIVO = "pesquisas_data.txt";

    public void salvarTodas(List<Pesquisa> pesquisas) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            for (Pesquisa p : pesquisas) {
                bw.write(p.getCodigo() + ";" +
                        p.getNome() + ";" +
                        p.getCargo() + ";" +
                        p.getTipo() + ";" +
                        p.getSituacao() + ";" +
                        p.getAbrangencia() + ";" +
                        (p.getDataInicio() != null ? p.getDataInicio().toString() : "") + ";" +
                        (p.getDataFim() != null ? p.getDataFim().toString() : "") + ";" +
                        p.getEntrevistasPrevistas() + ";" +
                        p.getMetodologia().replace("\n", " "));
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
        String[] partes = linha.split(";");
        if (partes.length >= 10) {
            final String codigo = partes[0];
            final String nome = partes[1];
            final String cargo = partes[2];
            final String tipo = partes[3];
            final String situacao = partes[4];
            final String abrangencia = partes[5];
            final LocalDate dataInicio = !partes[6].isEmpty() ? LocalDate.parse(partes[6]) : null;
            final LocalDate dataFim = !partes[7].isEmpty() ? LocalDate.parse(partes[7]) : null;
            final int entrevistas = Integer.parseInt(partes[8]);
            final String metodologia = partes[9];

            return new Pesquisa(codigo, nome, cargo, tipo, situacao, abrangencia,
                    dataInicio, dataFim, entrevistas, metodologia, new ArrayList<>());
        }
        return null;
    }
}