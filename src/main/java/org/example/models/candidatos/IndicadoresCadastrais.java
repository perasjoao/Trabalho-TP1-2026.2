package org.example.models.candidatos;

import org.example.models.comum.Cargo;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

//calculo sera implementado na etapa 3
public class IndicadoresCadastrais {

    private int totalCandidatos;
    private int totalCandidaturas;
    private int candidaturasAtivas;
    private int candidaturasInativas;
    private int totalPartidos;
    private int partidosAtivos;
    private double mediaCandidaturasPorPartido;
    private final Map<Cargo, Integer> candidaturasPorCargo = new EnumMap<>(Cargo.class);
    private final Map<String, Integer> candidaturasPorPartido = new LinkedHashMap<>();

    public int getTotalCandidatos() {
        return totalCandidatos;
    }

    public int getTotalCandidaturas() {
        return totalCandidaturas;
    }

    public int getCandidaturasAtivas() {
        return candidaturasAtivas;
    }

    public int getCandidaturasInativas() {
        return candidaturasInativas;
    }

    public int getTotalPartidos() {
        return totalPartidos;
    }

    public int getPartidosAtivos() {
        return partidosAtivos;
    }

    public double getMediaCandidaturasPorPartido() {
        return mediaCandidaturasPorPartido;
    }

    public Map<Cargo, Integer> getCandidaturasPorCargo() {
        return Collections.unmodifiableMap(candidaturasPorCargo);
    }

    public Map<String, Integer> getCandidaturasPorPartido() {
        return Collections.unmodifiableMap(candidaturasPorPartido);
    }
}