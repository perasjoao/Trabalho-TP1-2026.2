package org.example.candidatos.aplicacao;

import org.example.candidatos.dominio.Partido;
import org.example.comum.SituacaoPartido;

import java.util.ArrayList;
import java.util.List;

// Casos de uso de partidos (RF-CAN-3)
public class PartidoControlador {

    private final List<Partido> partidos = new ArrayList<>();

    public Partido cadastrar(String nome, String sigla, int numero, SituacaoPartido situacao) {
        validarUnicidade(sigla, numero, null);
        Partido partido = new Partido(nome, sigla, numero, situacao);
        partidos.add(partido);
        return partido;
    }

    public void editar(Partido partido, String nome, String sigla, int numero, SituacaoPartido situacao) {
        validarUnicidade(sigla, numero, partido);
        partido.setNome(nome);
        partido.setSigla(sigla);
        partido.setNumero(numero);
        partido.setSituacao(situacao);
    }

    // RN-CAN-9: partido com candidatos nao pode ser excluido
    public void excluir(Partido partido) {
        if (partido.possuiCandidatos()) {
            throw new IllegalStateException("O partido " + partido.getSigla()
                    + " possui candidatos associados e não pode ser excluído.");
        }
        partidos.remove(partido);
    }

    public List<Partido> listar() {
        return new ArrayList<>(partidos);
    }

    // busca por nome, sigla, numero ou situacao; criterios nulos sao ignorados
    public List<Partido> buscar(String nome, String sigla, Integer numero, SituacaoPartido situacao) {
        List<Partido> encontrados = new ArrayList<>();
        for (Partido p : partidos) {
            boolean ok = (nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase()))
                    && (sigla == null || p.getSigla().equalsIgnoreCase(sigla))
                    && (numero == null || p.getNumero() == numero)
                    && (situacao == null || p.getSituacao() == situacao);
            if (ok) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }

    // RN-CAN-1: sigla e numero nao se repetem
    private void validarUnicidade(String sigla, int numero, Partido atual) {
        for (Partido p : partidos) {
            if (p == atual) {
                continue;
            }
            if (p.getSigla().equalsIgnoreCase(sigla)) {
                throw new IllegalArgumentException("Já existe um partido com a sigla " + sigla + ".");
            }
            if (p.getNumero() == numero) {
                throw new IllegalArgumentException("Já existe um partido com o número " + numero + ".");
            }
        }
    }
}
