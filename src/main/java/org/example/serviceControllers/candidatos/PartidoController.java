package org.example.serviceControllers.candidatos;

import org.example.exceptions.DadoInvalidoException;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoPartido;

import java.util.ArrayList;
import java.util.List;

//operacoes basicas de partidos, com os dados em memoria
public class PartidoController {

    private final List<Partido> partidos = new ArrayList<>();

    public Partido cadastrar(String nome, String sigla, int numero, SituacaoPartido situacao)
            throws DadoInvalidoException {
        Partido partido = new Partido(nome, sigla, numero, situacao);
        partidos.add(partido);
        return partido;
    }

    public void editar(Partido partido, String nome, String sigla, int numero, SituacaoPartido situacao)
            throws DadoInvalidoException {
        partido.setNome(nome);
        partido.setSigla(sigla);
        partido.setNumero(numero);
        partido.setSituacao(situacao);
    }

    public void excluir(Partido partido) {
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
}