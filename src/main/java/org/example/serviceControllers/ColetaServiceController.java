package org.example.serviceControllers;

import org.example.models.Entrevista;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.utils.GerenciadorArquivo;
import org.example.viewControllers.ResearchController.ConsultasPesquisasController;

import java.util.ArrayList;
import java.util.List;

public class ColetaServiceController {

    private static final String CAMINHO_ARQUIVO = "entrevistas.dat";
    private final GerenciadorArquivo<Entrevista> gerenciadorArquivo = new GerenciadorArquivo<>();

    private List<Entrevista> entrevistas;
    private Entrevistador entrevistadorAtual;
    private Participante participanteAtual;

    public ColetaServiceController() {
        // Carrega as entrevistas salvas no arquivo quando iniciar
        this.entrevistas = gerenciadorArquivo.ler(CAMINHO_ARQUIVO);
        if (this.entrevistas == null) {
            this.entrevistas = new ArrayList<>();
        }
    }

    public void salvarConfiguracaoAmostra(Entrevistador entrevistador, Participante participante) {
        this.entrevistadorAtual = entrevistador;
        this.participanteAtual = participante;
    }

    public void salvarEntrevista(Entrevista entrevista) {
        if (entrevista != null) {
            this.entrevistas.add(entrevista);
            // Grava no arquivo .dat no disco
            gerenciadorArquivo.salvar(CAMINHO_ARQUIVO, this.entrevistas);
            // Atualiza imediatamente a contagem na tela de Consulta de Pesquisas
            ConsultasPesquisasController.setEntrevistasDoSistema(this.entrevistas);
        }
    }

    public List<Entrevista> listarEntrevistas() {
        return entrevistas;
    }

    public List<Entrevista> listarEntrevistasPorPesquisa(String codigoPesquisa) {
        List<Entrevista> filtradas = new ArrayList<>();
        if (codigoPesquisa == null || codigoPesquisa.trim().isEmpty()) {
            return filtradas;
        }
        String codAlvo = codigoPesquisa.trim();
        for (Entrevista e : entrevistas) {
            if (e != null && e.getCodigoPesquisa() != null) {
                String codEntrevista = e.getCodigoPesquisa().trim();
                if (codEntrevista.equalsIgnoreCase(codAlvo)
                        || codEntrevista.toLowerCase().startsWith(codAlvo.toLowerCase() + " - ")) {
                    filtradas.add(e);
                }
            }
        }
        return filtradas;
    }

    public Entrevistador getEntrevistadorAtual() {
        return entrevistadorAtual;
    }

    public Participante getParticipanteAtual() {
        return participanteAtual;
    }
}