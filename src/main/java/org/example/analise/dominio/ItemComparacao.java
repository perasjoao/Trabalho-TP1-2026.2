package org.example.analise.dominio;

// Uma linha da comparacao: o percentual da mesma opcao em duas pesquisas
public class ItemComparacao {

    private final String rotulo;
    private final double percentualA;
    private final double percentualB;

    public ItemComparacao(String rotulo, double percentualA, double percentualB) {
        this.rotulo = rotulo;
        this.percentualA = percentualA;
        this.percentualB = percentualB;
    }

    public String getRotulo() {
        return rotulo;
    }

    public double getPercentualA() {
        return percentualA;
    }

    public double getPercentualB() {
        return percentualB;
    }

    // variacao em pontos percentuais da pesquisa A para a B
    public double getDiferenca() {
        return CalculadoraEstatistica.arredondar(percentualB - percentualA);
    }

    @Override
    public String toString() {
        return rotulo + ": " + percentualA + "% -> " + percentualB + "% (" + getDiferenca() + " p.p.)";
    }
}
