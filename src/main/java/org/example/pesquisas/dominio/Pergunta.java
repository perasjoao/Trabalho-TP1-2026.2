package org.example.pesquisas.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pergunta de uma pesquisa (RF-PES-4). Classe abstrata: cada tipo de pergunta
// decide como suas opcoes sao formadas (ver as subclasses).
public abstract class Pergunta {

    private String texto;
    private final boolean obrigatoria;
    private final List<OpcaoResposta> opcoes = new ArrayList<>();

    protected Pergunta(String texto, boolean obrigatoria) {
        setTexto(texto);
        this.obrigatoria = obrigatoria;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O texto da pergunta é obrigatório.");
        }
        this.texto = texto;
    }

    public boolean isObrigatoria() {
        return obrigatoria;
    }

    public List<OpcaoResposta> getOpcoes() {
        return Collections.unmodifiableList(opcoes);
    }

    public boolean possuiOpcao(OpcaoResposta opcao) {
        return opcoes.contains(opcao);
    }

    // subclasses podem restringir quais opcoes sao aceitas
    public void adicionarOpcao(OpcaoResposta opcao) {
        opcoes.add(opcao);
    }

    public void removerOpcao(OpcaoResposta opcao) {
        opcoes.remove(opcao);
    }

    // metodo abstrato: cada tipo de pergunta informa o proprio nome
    public abstract String getTipo();

    @Override
    public String toString() {
        return texto + " [" + getTipo() + "]";
    }
}
