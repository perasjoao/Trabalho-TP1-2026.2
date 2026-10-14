package org.example.pesquisas.dominio;

// Andamento de uma pesquisa: previsto x realizado (RF-PES-9)
public class AndamentoPesquisa {

    private final int prevista;
    private final int realizada;

    public AndamentoPesquisa(int prevista, int realizada) {
        this.prevista = prevista;
        this.realizada = realizada;
    }

    public int getPrevista() {
        return prevista;
    }

    public int getRealizada() {
        return realizada;
    }

    public double getPercentualConclusao() {
        return prevista == 0 ? 0 : realizada * 100.0 / prevista;
    }
}
