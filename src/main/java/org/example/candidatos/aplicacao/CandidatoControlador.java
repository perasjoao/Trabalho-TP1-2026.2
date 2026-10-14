package org.example.candidatos.aplicacao;

import org.example.candidatos.dominio.Candidato;
import org.example.candidatos.dominio.IndicadoresCadastrais;
import org.example.candidatos.dominio.Partido;
import org.example.comum.Cargo;
import org.example.comum.SituacaoCandidatura;

import java.util.ArrayList;
import java.util.List;

// Casos de uso de candidatos (RF-CAN-1, 2, 4, 5 e 6)
public class CandidatoControlador {

    private final List<Candidato> candidatos = new ArrayList<>();

    public Candidato cadastrar(String nome, int numero, Cargo cargo, SituacaoCandidatura situacao) {
        validarNumero(numero, cargo, null);
        Candidato candidato = new Candidato(nome, numero, cargo, situacao);
        candidatos.add(candidato);
        return candidato;
    }

    public void editar(Candidato candidato, String nome, int numero, Cargo cargo) {
        validarNumero(numero, cargo, candidato);
        candidato.setNome(nome);
        candidato.setNumero(numero);
        candidato.setCargo(cargo);
    }

    // RF-CAN-2
    public void alterarSituacao(Candidato candidato, SituacaoCandidatura situacao) {
        candidato.setSituacao(situacao);
    }

    // RN-CAN-10: o partido continua existindo; so perde o candidato
    public void excluir(Candidato candidato) {
        if (candidato.getPartido() != null) {
            candidato.getPartido().removerCandidato(candidato);
        }
        candidatos.remove(candidato);
    }

    public List<Candidato> listar() {
        return new ArrayList<>(candidatos);
    }

    // RF-CAN-4
    public void associarAPartido(Candidato candidato, Partido partido) {
        partido.adicionarCandidato(candidato);
    }

    public void desassociarDoPartido(Candidato candidato) {
        if (candidato.getPartido() != null) {
            candidato.getPartido().removerCandidato(candidato);
        }
    }

    // RF-CAN-5: consulta por cargo, partido e situacao (nulos sao ignorados).
    // A consulta por pesquisa usa a lista de candidatos da propria pesquisa.
    public List<Candidato> consultar(Cargo cargo, Partido partido, SituacaoCandidatura situacao) {
        List<Candidato> encontrados = new ArrayList<>();
        for (Candidato c : candidatos) {
            boolean ok = (cargo == null || c.getCargo() == cargo)
                    && (partido == null || c.getPartido() == partido)
                    && (situacao == null || c.getSituacao() == situacao);
            if (ok) {
                encontrados.add(c);
            }
        }
        return encontrados;
    }

    // RF-CAN-6
    public IndicadoresCadastrais gerarIndicadores(List<Partido> partidos) {
        return new IndicadoresCadastrais(candidatos, partidos);
    }

    // RN-CAN-2: o numero nao se repete dentro do mesmo cargo
    private void validarNumero(int numero, Cargo cargo, Candidato atual) {
        for (Candidato c : candidatos) {
            if (c != atual && c.getCargo() == cargo && c.getNumero() == numero) {
                throw new IllegalArgumentException("Já existe um candidato a " + cargo
                        + " com o número " + numero + ".");
            }
        }
    }
}
