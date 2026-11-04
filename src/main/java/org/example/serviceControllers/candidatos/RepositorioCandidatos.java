package org.example.serviceControllers.candidatos;

import org.example.models.candidatos.Candidato;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.Partido;
import org.example.utils.GerenciadorArquivo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//um unico repositorio no programa, compartilhado pelos dois
//controllers e por todas as telas do modulo.
//Candidatos, candidaturas e partidos sao gravados juntos, num arquivo so, para que as
//ligacoes entre partido e candidatura continuem apontando para os mesmos objetos ao ler
public class RepositorioCandidatos implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String ARQUIVO = "candidatos_partidos.dat";
    private static RepositorioCandidatos instancia;

    private final List<Candidato> candidatos = new ArrayList<>();
    private final List<Candidatura> candidaturas = new ArrayList<>();
    private final List<Partido> partidos = new ArrayList<>();

    //so a propria classe cria o repositorio
    private RepositorioCandidatos() {
    }

    //na primeira chamada le o arquivo
    //se ele nao existir, comeca vazio
    public static RepositorioCandidatos getInstancia() {
        if (instancia == null) {
            List<RepositorioCandidatos> lido = new GerenciadorArquivo<RepositorioCandidatos>().ler(ARQUIVO);
            instancia = lido.isEmpty() ? new RepositorioCandidatos() : lido.get(0);
        }
        return instancia;
    }

    //grava as tres listas de uma vez, chamado pelos controllers apos cada alteracao
    void salvar() {
        List<RepositorioCandidatos> conteudo = new ArrayList<>();
        conteudo.add(this);
        new GerenciadorArquivo<RepositorioCandidatos>().salvar(ARQUIVO, conteudo);
    }

    //so os controllers deste pacote mexem nas listas
    List<Candidato> getCandidatos() {
        return candidatos;
    }

    List<Candidatura> getCandidaturas() {
        return candidaturas;
    }

    List<Partido> getPartidos() {
        return partidos;
    }
}
