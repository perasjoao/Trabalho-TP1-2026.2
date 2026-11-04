package org.example.serviceControllers.candidatos;

import org.example.exceptions.CandidaturaDuplicadaException;
import org.example.exceptions.DadoInvalidoException;
import org.example.models.candidatos.Candidato;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoCandidatura;
import org.example.models.comum.Cargo;

import java.util.ArrayList;
import java.util.List;

//operacoes de candidatos e candidaturas com as regras de negocio
//cada operacao valida tudo antes de alterar as listas, para nada ficar pela metade
//os dados ficam no repositorio unico e sao gravados em arquivo a cada alteracao
public class CandidatoController {
    private final RepositorioCandidatos repositorio = RepositorioCandidatos.getInstancia();
    private final List<Candidato> candidatos = repositorio.getCandidatos();
    private final List<Candidatura> candidaturas = repositorio.getCandidaturas();

    public Candidatura cadastrar(String nome, int numero, Cargo cargo, SituacaoCandidatura situacao)
            throws DadoInvalidoException, CandidaturaDuplicadaException {
        //cria os dois objetos primeiro (valida os dados) e so guarda se tudo for aceito
        Candidato candidato = new Candidato(nome);
        Candidatura candidatura = new Candidatura(candidato, numero, cargo, situacao);
        validarNumero(numero, cargo, null); //RN-CAN-2
        candidatos.add(candidato);
        candidaturas.add(candidatura);
        repositorio.salvar();
        return candidatura;
    }

    public void editar(Candidatura candidatura, String nome, int numero, Cargo cargo)
            throws DadoInvalidoException, CandidaturaDuplicadaException {
        //valida os dados novos em objetos temporarios antes de mexer na candidatura real
        new Candidatura(new Candidato(nome), numero, cargo, candidatura.getSituacao());
        validarNumero(numero, cargo, candidatura);
        candidatura.getCandidato().setNome(nome);
        candidatura.setNumero(numero);
        candidatura.setCargo(cargo);
        repositorio.salvar();
    }

    public void alterarSituacao(Candidatura candidatura, SituacaoCandidatura situacao) throws DadoInvalidoException {
        candidatura.setSituacao(situacao);
        repositorio.salvar();
    }

    //o partido tira a candidatura do partido anterior, se houver
    public void associarAPartido(Candidatura candidatura, Partido partido) throws DadoInvalidoException {
        if (partido == null) {
            throw new DadoInvalidoException("Selecione um partido para a associação.");
        }
        partido.adicionarCandidatura(candidatura);
        repositorio.salvar();
    }

    //a exclusao da candidatura nao exclui o partido, so desfaz a associacao
    public void excluir(Candidatura candidatura) {
        if (candidatura.getPartido() != null) {
            candidatura.getPartido().removerCandidatura(candidatura);
        }
        candidaturas.remove(candidatura);
        repositorio.salvar();
    }

    public List<Candidatura> listar() {
        return new ArrayList<>(candidaturas);
    }

    //criterios nulos sao ignorados, os preenchidos se combinam
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

    //o numero nao pode se repetir dentro do mesmo cargo
    //(a candidatura que esta sendo editada nao conta como repetida dela mesma)
    private void validarNumero(int numero, Cargo cargo, Candidatura atual) throws CandidaturaDuplicadaException {
        for (Candidatura c : candidaturas) {
            if (c != atual && c.getCargo() == cargo && c.getNumero() == numero) {
                throw new CandidaturaDuplicadaException("Já existe uma candidatura a " + cargo
                        + " com o número " + numero + ".");
            }
        }
    }
}
