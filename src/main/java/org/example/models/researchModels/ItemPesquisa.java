package org.example.models.researchModels;

public abstract class ItemPesquisa {
    private final String id;
    private String enunciado;

    public ItemPesquisa(String id, String enunciado) {
        this.id = id;
        this.enunciado = enunciado;
    }

    public String getId() { return id; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
}