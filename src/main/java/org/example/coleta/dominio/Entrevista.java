package org.example.coleta.dominio;

import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Uma entrevista: um participante respondendo uma pesquisa (RF-COL-3, RN-COL-1 e RN-COL-5).
public class Entrevista {

    private final String codigo;
    private final Pesquisa pesquisa;
    private final Participante participante;
    private final Entrevistador entrevistador;
    private final LocalDate data;
    private final Map<Pergunta, Resposta> respostas = new LinkedHashMap<>();
    private boolean concluida;

    public Entrevista(String codigo, Pesquisa pesquisa, Participante participante,
                      Entrevistador entrevistador, LocalDate data) {
        if (pesquisa == null || participante == null) {
            throw new IllegalArgumentException("A entrevista precisa de uma pesquisa e de um participante.");
        }
        this.codigo = codigo;
        this.pesquisa = pesquisa;
        this.participante = participante;
        this.entrevistador = entrevistador;
        this.data = data;
    }

    // RN-COL-6 (uma resposta por pergunta) e RN-COL-7 (concluida nao muda)
    public void registrarResposta(Resposta resposta) {
        if (concluida) {
            throw new IllegalStateException("A entrevista " + codigo + " já foi concluída e não pode ser alterada.");
        }
        if (!pesquisa.getPerguntas().contains(resposta.getPergunta())) {
            throw new IllegalArgumentException("A pergunta não pertence à pesquisa " + pesquisa.getCodigo() + ".");
        }
        if (respostas.containsKey(resposta.getPergunta())) {
            throw new IllegalStateException("A pergunta \"" + resposta.getPergunta().getTexto() + "\" já foi respondida.");
        }
        respostas.put(resposta.getPergunta(), resposta);
    }

    // so conclui se todas as perguntas obrigatorias tiverem resposta
    public void concluir() {
        for (Pergunta p : pesquisa.getPerguntas()) {
            if (p.isObrigatoria() && !respostas.containsKey(p)) {
                throw new IllegalStateException("Falta responder a pergunta obrigatória \"" + p.getTexto() + "\".");
            }
        }
        concluida = true;
    }

    public Resposta getResposta(Pergunta pergunta) {
        return respostas.get(pergunta);
    }

    public List<Resposta> getRespostas() {
        return new ArrayList<>(respostas.values());
    }

    public String getCodigo() {
        return codigo;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public Participante getParticipante() {
        return participante;
    }

    public Entrevistador getEntrevistador() {
        return entrevistador;
    }

    public LocalDate getData() {
        return data;
    }

    public boolean isConcluida() {
        return concluida;
    }
}
