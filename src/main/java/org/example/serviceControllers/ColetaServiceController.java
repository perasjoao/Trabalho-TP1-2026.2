package org.example.serviceControllers;

import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.models.Entrevista;

import java.util.ArrayList;
import java.util.List;

public class ColetaServiceController {

    private final List<Entrevista> entrevistas = new ArrayList<>();

    // Guarda a configuração atual do participante/entrevistador
    private Entrevistador entrevistadorAtual;
    private Participante participanteAtual;

    public void salvarConfiguracaoAmostra(Entrevistador entrevistador, Participante participante) {
        this.entrevistadorAtual = entrevistador;
        this.participanteAtual = participante;
    }

    public void salvarEntrevista(Entrevista entrevista) {
        this.entrevistas.add(entrevista);
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