package org.example.models.researchModels;

public class OpcaoResposta {
    private int numero;
    private String texto;

    public OpcaoResposta(int numero, String texto) {
        this.numero = numero;
        this.texto = texto;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    @Override
    public String toString() {
        return numero + " - " + texto;
    }
}