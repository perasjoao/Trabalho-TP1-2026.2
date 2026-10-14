package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Resposta;

// Padrao Strategy: define "em qual grupo uma resposta entra".
// A CalculadoraEstatistica so conhece esta classe abstrata; trocar o criterio
// (por candidato, por regiao, combinado...) muda o resultado sem mudar a calculadora.
public abstract class CriterioAgrupamento {

    private final String nome;

    protected CriterioAgrupamento(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    // devolve o rotulo do grupo da resposta, ex.: "Candidato A", "Norte", "Candidato A | Norte"
    public abstract String classificar(Entrevista entrevista, Resposta resposta);
}
