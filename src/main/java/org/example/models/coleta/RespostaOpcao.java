package org.example.models.coleta;

//resposta em que o participante escolheu uma das opcoes cadastradas na pergunta
public class RespostaOpcao extends Resposta {
    private static final long serialVersionUID = 1L;

    public RespostaOpcao(String idPergunta, String textoPergunta, String opcao) {
        super(idPergunta, textoPergunta, opcao);
    }

    @Override
    public String getTipo() {
        return "Opção";
    }

    @Override
    public boolean contaNoPercentual() {
        return true;
    }
}
