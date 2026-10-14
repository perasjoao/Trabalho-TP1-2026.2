package org.example.coleta.dominio;

import org.example.comum.RespostaEspecial;
import org.example.pesquisas.dominio.OpcaoResposta;
import org.example.pesquisas.dominio.Pergunta;

// Resposta de um participante a uma pergunta (RF-COL-2, 4 e 5).
// Tem uma opcao escolhida OU uma resposta especial ("não sabe"...), nunca as duas.
public class Resposta {

    private final Pergunta pergunta;
    private final OpcaoResposta opcaoEscolhida;
    private final RespostaEspecial respostaEspecial;

    private Resposta(Pergunta pergunta, OpcaoResposta opcao, RespostaEspecial especial) {
        this.pergunta = pergunta;
        this.opcaoEscolhida = opcao;
        this.respostaEspecial = especial;
    }

    // metodos de criacao com nomes claros, no lugar de construtores publicos
    public static Resposta comOpcao(Pergunta pergunta, OpcaoResposta opcao) {
        if (!pergunta.possuiOpcao(opcao)) {
            throw new IllegalArgumentException("A opção \"" + opcao + "\" não pertence à pergunta \"" + pergunta.getTexto() + "\".");
        }
        return new Resposta(pergunta, opcao, null);
    }

    public static Resposta especial(Pergunta pergunta, RespostaEspecial tipo) {
        return new Resposta(pergunta, null, tipo);
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public OpcaoResposta getOpcaoEscolhida() {
        return opcaoEscolhida;
    }

    public RespostaEspecial getRespostaEspecial() {
        return respostaEspecial;
    }

    // valida = escolheu uma opcao; e a base dos percentuais (RN-ANA-1)
    public boolean isValida() {
        return opcaoEscolhida != null;
    }

    @Override
    public String toString() {
        return isValida() ? opcaoEscolhida.getTexto() : respostaEspecial.getDescricao();
    }
}
