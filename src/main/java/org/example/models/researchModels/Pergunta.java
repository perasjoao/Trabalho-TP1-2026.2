package org.example.models.researchModels;

import org.example.models.candidatos.Candidatura;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pergunta extends ItemPesquisa {
    private String tipo;
    private List<OpcaoResposta> opcoes;
    private List<Candidatura> candidaturasAssociadas;
    private List<Integer> numerosCandidaturasSalvas;
    private boolean paraTodosCandidatos;

    public Pergunta(String id, String enunciado, String tipo, List<OpcaoResposta> opcoes) {
        super(id, enunciado);
        this.tipo = tipo;
        this.opcoes = (opcoes != null) ? opcoes : new ArrayList<>();
        this.candidaturasAssociadas = new ArrayList<>();
        this.numerosCandidaturasSalvas = new ArrayList<>();
        this.paraTodosCandidatos = false;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<OpcaoResposta> getOpcoes() { return opcoes; }
    public void setOpcoes(List<OpcaoResposta> opcoes) { this.opcoes = opcoes; }

    public List<Candidatura> getCandidaturasAssociadas() { return candidaturasAssociadas; }
    public void setCandidaturasAssociadas(List<Candidatura> candidaturasAssociadas) {
        this.candidaturasAssociadas = (candidaturasAssociadas != null) ? candidaturasAssociadas : new ArrayList<>();
        this.numerosCandidaturasSalvas.clear();
        for (Candidatura c : this.candidaturasAssociadas) {
            if (c != null && !this.numerosCandidaturasSalvas.contains(c.getNumero())) {
                this.numerosCandidaturasSalvas.add(c.getNumero());
            }
        }
    }

    public List<Integer> getNumerosCandidaturasSalvas() { return numerosCandidaturasSalvas; }
    public void setNumerosCandidaturasSalvas(List<Integer> numerosCandidaturasSalvas) {
        this.numerosCandidaturasSalvas = (numerosCandidaturasSalvas != null) ? numerosCandidaturasSalvas : new ArrayList<>();
    }

    public boolean isParaTodosCandidatos() { return paraTodosCandidatos; }
    public void setParaTodosCandidatos(boolean paraTodosCandidatos) {
        this.paraTodosCandidatos = paraTodosCandidatos;
    }

    public boolean refereSeAoCandidato(Candidatura candidato) {
        if (paraTodosCandidatos) return true;
        if (candidato == null) return false;
        return refereSeAoNumeroCandidato(candidato.getNumero());
    }

    public boolean refereSeAoNumeroCandidato(int numeroCandidato) {
        if (paraTodosCandidatos) return true;
        return candidaturasAssociadas.stream().anyMatch(c -> c.getNumero() == numeroCandidato)
                || numerosCandidaturasSalvas.contains(numeroCandidato);
    }

    public String getResumoCandidatos() {
        if (paraTodosCandidatos) {
            return "Todos";
        }
        if (!candidaturasAssociadas.isEmpty()) {
            return candidaturasAssociadas.stream()
                    .map(c -> c.getCandidato().getNome() + " (" + c.getNumero() + ")")
                    .collect(Collectors.joining(", "));
        }
        if (!numerosCandidaturasSalvas.isEmpty()) {
            return numerosCandidaturasSalvas.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
        }
        return "Geral";
    }

    public void adicionarOpcao(OpcaoResposta opcao) {
        if (opcao != null) {
            this.opcoes.add(opcao);
        }
    }

    public void removerOpcao(OpcaoResposta opcao) {
        this.opcoes.remove(opcao);
    }
}