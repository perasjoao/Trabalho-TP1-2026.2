package org.example.coleta.aplicacao;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Entrevistador;
import org.example.coleta.dominio.Participante;
import org.example.coleta.dominio.Resposta;
import org.example.comum.RespostaEspecial;
import org.example.pesquisas.dominio.OpcaoResposta;
import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Casos de uso da coleta: amostra, registro de respostas e consulta das entrevistas.
public class ColetaControlador {

    private final List<Entrevistador> entrevistadores = new ArrayList<>();
    private final List<Participante> participantes = new ArrayList<>();
    private final List<Entrevista> entrevistas = new ArrayList<>();

    // RF-COL-1
    public Entrevistador cadastrarEntrevistador(String codigo, String nome) {
        Entrevistador e = new Entrevistador(codigo, nome);
        entrevistadores.add(e);
        return e;
    }

    public Participante cadastrarParticipante(String codigo, String faixaEtaria, String escolaridade,
                                              String genero, String regiao, String faixaRenda) {
        Participante p = new Participante(codigo, faixaEtaria, escolaridade, genero, regiao, faixaRenda);
        participantes.add(p);
        return p;
    }

    // RN-COL-1, RN-COL-2, RN-PES-7 e RN-COL-8
    public Entrevista iniciarEntrevista(String codigo, Pesquisa pesquisa, Participante participante,
                                        Entrevistador entrevistador, LocalDate data) {
        if (!pesquisa.podeReceberEntrevistas()) {
            throw new IllegalStateException("A pesquisa " + pesquisa.getCodigo() + " está " + pesquisa.getSituacao()
                    + " e não pode receber entrevistas.");
        }
        for (Entrevista e : listarPorPesquisa(pesquisa)) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) {
                throw new IllegalArgumentException("A entrevista " + codigo + " já foi registrada nesta pesquisa.");
            }
        }
        if (contarRealizadas(pesquisa) >= pesquisa.getQuantidadePrevista()) {
            throw new IllegalStateException("A pesquisa já atingiu a quantidade prevista de entrevistas.");
        }
        Entrevista entrevista = new Entrevista(codigo, pesquisa, participante, entrevistador, data);
        entrevistas.add(entrevista);
        return entrevista;
    }

    // RF-COL-2 e RF-COL-4
    public void registrarResposta(Entrevista entrevista, Pergunta pergunta, OpcaoResposta opcao) {
        entrevista.registrarResposta(Resposta.comOpcao(pergunta, opcao));
    }

    // RF-COL-5
    public void registrarRespostaEspecial(Entrevista entrevista, Pergunta pergunta, RespostaEspecial tipo) {
        entrevista.registrarResposta(Resposta.especial(pergunta, tipo));
    }

    public void concluirEntrevista(Entrevista entrevista) {
        entrevista.concluir();
    }

    public List<Entrevista> listarPorPesquisa(Pesquisa pesquisa) {
        List<Entrevista> daPesquisa = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            if (e.getPesquisa() == pesquisa) {
                daPesquisa.add(e);
            }
        }
        return daPesquisa;
    }

    public int contarRealizadas(Pesquisa pesquisa) {
        return listarPorPesquisa(pesquisa).size();
    }

    // RF-COL-6: filtros nulos sao ignorados
    public List<Entrevista> filtrar(Pesquisa pesquisa, String faixaEtaria, String escolaridade, String genero,
                                    String regiao, String faixaRenda, String codigoEntrevistador) {
        List<Entrevista> encontradas = new ArrayList<>();
        for (Entrevista e : entrevistas) {
            Participante p = e.getParticipante();
            boolean ok = (pesquisa == null || e.getPesquisa() == pesquisa)
                    && igualOuIgnorado(faixaEtaria, p.getFaixaEtaria())
                    && igualOuIgnorado(escolaridade, p.getEscolaridade())
                    && igualOuIgnorado(genero, p.getGenero())
                    && igualOuIgnorado(regiao, p.getRegiao())
                    && igualOuIgnorado(faixaRenda, p.getFaixaRenda())
                    && (codigoEntrevistador == null || (e.getEntrevistador() != null
                        && e.getEntrevistador().getCodigo().equalsIgnoreCase(codigoEntrevistador)));
            if (ok) {
                encontradas.add(e);
            }
        }
        return encontradas;
    }

    private static boolean igualOuIgnorado(String filtro, String valor) {
        return filtro == null || filtro.equalsIgnoreCase(valor);
    }
}
