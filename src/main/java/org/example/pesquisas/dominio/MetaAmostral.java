package org.example.pesquisas.dominio;

// Meta do plano amostral para um grupo, por exemplo: Região = Norte, minimo 50 (RN-COL-9).
public class MetaAmostral {

    private final String caracteristica;
    private final String valor;
    private final int minimo;

    public MetaAmostral(String caracteristica, String valor, int minimo) {
        if (minimo <= 0) {
            throw new IllegalArgumentException("A meta mínima deve ser positiva.");
        }
        this.caracteristica = caracteristica;
        this.valor = valor;
        this.minimo = minimo;
    }

    public String getCaracteristica() {
        return caracteristica;
    }

    public String getValor() {
        return valor;
    }

    public int getMinimo() {
        return minimo;
    }

    // RN-PES-10: a meta nao foi atingida quando o grupo tem menos entrevistas que o minimo
    public boolean atingidaPor(int entrevistasValidas) {
        return entrevistasValidas >= minimo;
    }

    @Override
    public String toString() {
        return caracteristica + " = " + valor + " (mínimo " + minimo + ")";
    }
}
