package org.example.pesquisas.dominio;

// Pergunta com as opcoes fixas "Sim" e "Não".
public class PerguntaSimNao extends Pergunta {

    public PerguntaSimNao(String texto, boolean obrigatoria) {
        super(texto, obrigatoria);
        super.adicionarOpcao(new OpcaoResposta("Sim"));
        super.adicionarOpcao(new OpcaoResposta("Não"));
    }

    // sobrescrita: as opcoes desta pergunta nao podem ser alteradas
    @Override
    public void adicionarOpcao(OpcaoResposta opcao) {
        throw new UnsupportedOperationException("Uma pergunta Sim/Não não aceita outras opções.");
    }

    @Override
    public String getTipo() {
        return "Sim/Não";
    }
}
