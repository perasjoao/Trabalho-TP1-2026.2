package org.example.serviceControllers.candidatos;

import org.example.models.candidatos.Candidato;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoCandidatura;
import org.example.models.comum.Cargo;

import java.util.ArrayList;
import java.util.List;

//operacoes basicas de candidatos e candidaturas, com os dados em memoria
//numero unico por cargo entra na etapa 3
public class CandidatoController {
    private final List<Candidato> candidatos = new ArrayList<>();
    private final List<Candidatura> candidaturas = new ArrayList<>();

    public Candidatura cadastrar(String nome, int numero, Cargo cargo, SituacaoCandidatura situacao) {
        Candidato candidato = new Candidato(nome);
        candidatos.add(candidato);
        Candidatura candidatura = new Candidatura(candidato, numero, cargo, situacao);
        candidaturas.add(candidatura);
        return candidatura;
    }

    public void editar(Candidatura candidatura, String nome, int numero, Cargo cargo) {
        candidatura.getCandidato().setNome(nome);
        candidatura.setNumero(numero);
        candidatura.setCargo(cargo);
    }

    public void alterarSituacao(Candidatura candidatura, SituacaoCandidatura situacao) {
        candidatura.setSituacao(situacao);
    }

    public void associarAPartido(Candidatura candidatura, Partido partido) {
        partido.adicionarCandidatura(candidatura);
    }

    public void excluir(Candidatura candidatura) {
        if (candidatura.getPartido() != null) {
            candidatura.getPartido().removerCandidatura(candidatura);
        }
        candidaturas.remove(candidatura);
    }

    public List<Candidatura> listar() {
        return new ArrayList<>(candidaturas);
    }

    // criterios nulos sao ignorados, os preenchidos se combinam
    public List<Candidatura> consultar(Cargo cargo, Partido partido, SituacaoCandidatura situacao) {
        List<Candidatura> encontradas = new ArrayList<>();
        for (Candidatura c : candidaturas) {
            boolean cargoOk = (cargo == null) || (c.getCargo() == cargo);
            boolean partidoOk = (partido == null) || (c.getPartido() == partido);
            boolean situacaoOk = (situacao == null) || (c.getSituacao() == situacao);
            if (cargoOk && partidoOk && situacaoOk) {
                encontradas.add(c);
            }
        }
        return encontradas;
    }
}