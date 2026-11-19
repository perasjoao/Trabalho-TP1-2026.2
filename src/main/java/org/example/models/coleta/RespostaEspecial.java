package org.example.models.coleta;

import java.util.List;

//"nao sabe", "nao respondeu" e "nenhuma das opcoes"
//vale pra qualquer pergunta e nao entra no calculo dos percentuais
public class RespostaEspecial extends Resposta {
    private static final long serialVersionUID = 1L;

    public static final List<String> OPCOES = List.of("Não sabe", "Não respondeu", "Nenhuma das opções");

    public RespostaEspecial(String idPergunta, String textoPergunta, String opcao) {
        super(idPergunta, textoPergunta, opcao);
        if (!OPCOES.contains(opcao)) {
            throw new IllegalArgumentException("Resposta especial inválida: " + opcao);
        }
    }

    @Override
    public String getTipo() {
        return "Especial";
    }

    @Override
    public boolean contaNoPercentual() {
        return false;
    }
}
