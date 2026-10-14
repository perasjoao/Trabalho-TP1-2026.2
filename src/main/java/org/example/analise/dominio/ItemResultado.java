package org.example.analise.dominio;

// Uma linha do resultado: rotulo, quantidade absoluta e percentual (RN-ANA-4)
public class ItemResultado {

    private final String rotulo;
    private final int quantidade;
    private final double percentual;

    public ItemResultado(String rotulo, int quantidade, double percentual) {
        this.rotulo = rotulo;
        this.quantidade = quantidade;
        this.percentual = percentual;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPercentual() {
        return percentual;
    }

    @Override
    public String toString() {
        return rotulo + ": " + quantidade + " (" + percentual + "%)";
    }
}
