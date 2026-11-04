package org.example.serviceControllers.candidatos;

import org.example.exceptions.DadoInvalidoException;
import org.example.exceptions.PartidoComCandidaturasException;
import org.example.exceptions.PartidoDuplicadoException;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoPartido;

import java.util.ArrayList;
import java.util.List;

//operacoes de partidos com as regras de negocio
//cada operacao valida tudo antes de alterar a lista, para nada ficar pela metade
public class PartidoController {

    private final List<Partido> partidos = new ArrayList<>();

    public Partido cadastrar(String nome, String sigla, int numero, SituacaoPartido situacao)
            throws DadoInvalidoException, PartidoDuplicadoException {
        Partido partido = new Partido(nome, sigla, numero, situacao); //valida os dados
        validarUnicidade(partido.getSigla(), partido.getNumero(), null);
        partidos.add(partido);
        return partido;
    }

    public void editar(Partido partido, String nome, String sigla, int numero, SituacaoPartido situacao)
            throws DadoInvalidoException, PartidoDuplicadoException {
        //valida os dados novos num objeto temporario antes de mexer no partido real
        Partido novo = new Partido(nome, sigla, numero, situacao);
        validarUnicidade(novo.getSigla(), novo.getNumero(), partido);
        partido.setNome(nome);
        partido.setSigla(sigla);
        partido.setNumero(numero);
        partido.setSituacao(situacao);
    }

    //partido com candidaturas nao pode ser excluido
    public void excluir(Partido partido) throws PartidoComCandidaturasException {
        if (partido.possuiCandidaturas()) {
            throw new PartidoComCandidaturasException("O partido " + partido.getSigla()
                    + " possui candidaturas associadas e não pode ser excluído.");
        }
        partidos.remove(partido);
    }

    public List<Partido> listar() {
        return new ArrayList<>(partidos);
    }

    //devolve o partido com a sigla informada, ou null se nao existir
    public Partido buscarPorSigla(String sigla) {
        for (Partido p : partidos) {
            if (p.getSigla().equalsIgnoreCase(sigla)) {
                return p;
            }
        }
        return null;
    }

    //nao podem existir dois partidos com a mesma sigla ou o mesmo numero
    //(o partido que esta sendo editado nao conta como repetido dele mesmo)
    private void validarUnicidade(String sigla, int numero, Partido atual) throws PartidoDuplicadoException {
        for (Partido p : partidos) {
            if (p == atual) {
                continue;
            }
            if (p.getSigla().equalsIgnoreCase(sigla)) {
                throw new PartidoDuplicadoException("Já existe um partido com a sigla " + sigla + ".");
            }
            if (p.getNumero() == numero) {
                throw new PartidoDuplicadoException("Já existe um partido com o número " + numero + ".");
            }
        }
    }
}
