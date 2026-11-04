package org.example.serviceControllers;

import org.example.models.Entrevista;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.utils.GerenciadorArquivo;

import java.util.ArrayList;
import java.util.List;

public class ColetaServiceController {

    private static final String CAMINHO_ARQUIVO = "entrevistas.dat";
    private final GerenciadorArquivo<Entrevista> gerenciadorArquivo = new GerenciadorArquivo<>();

    private List<Entrevista> entrevistas;
    private Entrevistador entrevistadorAtual;
    private Participante participanteAtual;

    public ColetaServiceController() {
        // Carrega  as entrevistas salvas no arquivo quando iniciar
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
        this.entrevistas.add(entrevista);
        // Grava no arquivo .dat no disco
        gerenciadorArquivo.salvar(CAMINHO_ARQUIVO, this.entrevistas);
    }

    public List<Entrevista> listarEntrevistas() {
        return entrevistas;
    }

    public Entrevistador getEntrevistadorAtual() {
        return entrevistadorAtual;
    }

    public Participante getParticipanteAtual() {
        return participanteAtual;
    }
}