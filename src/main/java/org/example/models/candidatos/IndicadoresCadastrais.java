package org.example.models.candidatos;

import org.example.models.comum.Cargo;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//indicadores calculados a partir dos cadastros, como uma fotografia do momento
//candidaturas e partidos inativos nao contam como ativos (usa o isAtivo de cada classe)
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

    public IndicadoresCadastrais(List<Candidatura> candidaturas, List<Partido> partidos) {
        //candidatos = pessoas com pelo menos uma candidatura (o Set nao repete a mesma pessoa)
        Set<Candidato> pessoas = new HashSet<>();
        for (Candidatura c : candidaturas) {
            pessoas.add(c.getCandidato());
            if (c.isAtivo()) {
                candidaturasAtivas++;
            }
            candidaturasPorCargo.merge(c.getCargo(), 1, Integer::sum);
        }
        totalCandidatos = pessoas.size();
        totalCandidaturas = candidaturas.size();
        candidaturasInativas = totalCandidaturas - candidaturasAtivas;

        int comPartido = 0;
        for (Partido p : partidos) {
            if (p.isAtivo()) {
                partidosAtivos++;
            }
            candidaturasPorPartido.put(p.getSigla(), p.getCandidaturas().size());
            comPartido += p.getCandidaturas().size();
        }
        totalPartidos = partidos.size();
        //sem partidos a media fica 0 (evita divisao por zero)
        mediaCandidaturasPorPartido = totalPartidos == 0 ? 0 : (double) comPartido / totalPartidos;
    }

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