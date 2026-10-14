package org.example.candidatos.dominio;

import org.example.comum.Cargo;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Indicadores calculados a partir dos cadastros (RF-CAN-6, RN-CAN-7 e RN-CAN-8)
public class IndicadoresCadastrais {

    private final int totalCandidatos;
    private final int candidaturasAtivas;
    private final int totalPartidos;
    private final int partidosAtivos;
    private final Map<Cargo, Integer> candidatosPorCargo = new EnumMap<>(Cargo.class);
    private final Map<String, Integer> candidatosPorPartido = new LinkedHashMap<>();

    public IndicadoresCadastrais(List<Candidato> candidatos, List<Partido> partidos) {
        totalCandidatos = candidatos.size();
        totalPartidos = partidos.size();
        candidaturasAtivas = (int) candidatos.stream().filter(Candidato::isCandidaturaAtiva).count();
        partidosAtivos = (int) partidos.stream().filter(Partido::isAtivo).count();
        for (Candidato c : candidatos) {
            candidatosPorCargo.merge(c.getCargo(), 1, Integer::sum);
        }
        for (Partido p : partidos) {
            candidatosPorPartido.put(p.getSigla(), p.getCandidatos().size());
        }
    }

    public int getTotalCandidatos() {
        return totalCandidatos;
    }

    public int getCandidaturasAtivas() {
        return candidaturasAtivas;
    }

    public int getCandidaturasInativas() {
        return totalCandidatos - candidaturasAtivas;
    }

    public int getTotalPartidos() {
        return totalPartidos;
    }

    public int getPartidosAtivos() {
        return partidosAtivos;
    }

    public Map<Cargo, Integer> getCandidatosPorCargo() {
        return Collections.unmodifiableMap(candidatosPorCargo);
    }

    public Map<String, Integer> getCandidatosPorPartido() {
        return Collections.unmodifiableMap(candidatosPorPartido);
    }

    public double getMediaCandidatosPorPartido() {
        return totalPartidos == 0 ? 0 : (double) totalCandidatos / totalPartidos;
    }
}
