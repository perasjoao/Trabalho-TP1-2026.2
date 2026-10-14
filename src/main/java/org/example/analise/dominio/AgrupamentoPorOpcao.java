package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Resposta;

// Agrupa pela opcao escolhida (em perguntas de candidatos, por candidato) - RF-ANA-1
public class AgrupamentoPorOpcao extends CriterioAgrupamento {

    public AgrupamentoPorOpcao() {
        super("Opção escolhida");
    }

    @Override
    public String classificar(Entrevista entrevista, Resposta resposta) {
        return resposta.getOpcaoEscolhida().getTexto();
    }
}
