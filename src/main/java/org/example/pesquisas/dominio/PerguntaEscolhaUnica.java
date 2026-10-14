package org.example.pesquisas.dominio;

// Pergunta com opcoes livres definidas pelo pesquisador; o participante escolhe uma.
public class PerguntaEscolhaUnica extends Pergunta {

    public PerguntaEscolhaUnica(String texto, boolean obrigatoria) {
        super(texto, obrigatoria);
    }

    public void adicionarOpcao(String texto) {
        adicionarOpcao(new OpcaoResposta(texto));
    }

    @Override
    public String getTipo() {
        return "Escolha única";
    }
}
