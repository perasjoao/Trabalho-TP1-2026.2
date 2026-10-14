package org.example.analise.dominio;

import java.time.LocalDateTime;

// Relatorio consolidado (RF-ANA-4): o resultado calculado e os filtros usados
public class Relatorio {

    private final String titulo;
    private final FiltroRelatorio filtro;
    private final ResultadoAnalise resultado;
    private final LocalDateTime geradoEm;

    public Relatorio(String titulo, FiltroRelatorio filtro, ResultadoAnalise resultado) {
        this.titulo = titulo;
        this.filtro = filtro;
        this.resultado = resultado;
        this.geradoEm = LocalDateTime.now();
    }

    public String getTitulo() {
        return titulo;
    }

    public FiltroRelatorio getFiltro() {
        return filtro;
    }

    public ResultadoAnalise getResultado() {
        return resultado;
    }

    public LocalDateTime getGeradoEm() {
        return geradoEm;
    }
}
