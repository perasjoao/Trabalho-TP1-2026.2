package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Resposta;

// Distribuicao conjunta (RF-ANA-2): combina dois criterios, ex.: candidato x faixa etaria.
// Usa composicao: guarda dois outros criterios e junta os rotulos deles.
public class AgrupamentoConjunto extends CriterioAgrupamento {

    private final CriterioAgrupamento primeiro;
    private final CriterioAgrupamento segundo;

    public AgrupamentoConjunto(CriterioAgrupamento primeiro, CriterioAgrupamento segundo) {
        super(primeiro.getNome() + " x " + segundo.getNome());
        this.primeiro = primeiro;
        this.segundo = segundo;
    }

    @Override
    public String classificar(Entrevista entrevista, Resposta resposta) {
        return primeiro.classificar(entrevista, resposta) + " | " + segundo.classificar(entrevista, resposta);
    }
}
