package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Resposta;

// Agrupa por uma caracteristica do participante (regiao, faixa etaria...) - RF-ANA-1
public class AgrupamentoPorCaracteristica extends CriterioAgrupamento {

    private final CaracteristicaAmostra caracteristica;

    public AgrupamentoPorCaracteristica(CaracteristicaAmostra caracteristica) {
        super(caracteristica.getDescricao());
        this.caracteristica = caracteristica;
    }

    @Override
    public String classificar(Entrevista entrevista, Resposta resposta) {
        return caracteristica.valorDe(entrevista.getParticipante());
    }
}
