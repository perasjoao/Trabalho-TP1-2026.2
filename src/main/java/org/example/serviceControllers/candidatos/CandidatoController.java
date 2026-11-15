package org.example.serviceControllers.candidatos;

import org.example.exceptions.CandidaturaDuplicadaException;
import org.example.exceptions.DadoInvalidoException;
import org.example.models.candidatos.Candidato;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.IndicadoresCadastrais;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoCandidatura;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;

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

    //indicadores cadastrais calculados com os dados atuais do repositorio
    public IndicadoresCadastrais gerarIndicadores() {
        return new IndicadoresCadastrais(candidaturas, repositorio.getPartidos());
    }

    //criterios nulos ou vazios sao ignorados, os preenchidos se combinam
    public List<Candidatura> consultar(String nome, Integer numero, Cargo cargo, Partido partido,
                                       SituacaoCandidatura situacao, Pesquisa pesquisa) {
        List<Candidatura> encontradas = new ArrayList<>();
        for (Candidatura c : candidaturas) {
            boolean nomeOk = nome == null || nome.isBlank()
                    || c.getCandidato().getNome().toLowerCase().contains(nome.trim().toLowerCase());
            boolean numeroOk = numero == null || c.getNumero() == numero;
            boolean cargoOk = cargo == null || c.getCargo() == cargo;
            boolean partidoOk = partido == null || c.getPartido() == partido;
            boolean situacaoOk = situacao == null || c.getSituacao() == situacao;
            boolean pesquisaOk = pesquisa == null || participaDaPesquisa(c, pesquisa);
            if (nomeOk && numeroOk && cargoOk && partidoOk && situacaoOk && pesquisaOk) {
                encontradas.add(c);
            }
        }
        return encontradas;
    }

    //a pesquisa guarda o cargo e os numeros das candidaturas associadas
    //numero + cargo identificam uma unica candidatura
    private boolean participaDaPesquisa(Candidatura c, Pesquisa pesquisa) {
        return c.getCargo() == pesquisa.getCargo()
                && pesquisa.getNumerosCandidaturasSalvas().contains(c.getNumero());
    }

    //o numero nao pode se repetir dentro do mesmo cargo
    //a candidatura que esta sendo editada nao conta como repetida dela mesma
    private void validarNumero(int numero, Cargo cargo, Candidatura atual) throws CandidaturaDuplicadaException {
        for (Candidatura c : candidaturas) {
            if (c != atual && c.getCargo() == cargo && c.getNumero() == numero) {
                throw new CandidaturaDuplicadaException("Já existe uma candidatura a " + cargo
                        + " com o número " + numero + ".");
            }
        }
    }
}
