package org.example.analise.dominio;

// Padrao Strategy para a exportacao (RF-ANA-6): cada formato implementa
// esta interface, e quem exporta nao precisa saber qual formato esta usando.
public interface Exportador {

    String exportar(Relatorio relatorio);

    String getExtensao();
}
