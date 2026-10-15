package org.example.models;

public class Pesquisador extends Usuario {
    Pesquisa pesquisa;

    @Override
    public String getCargo() {
        return "Pesquisaor";
    }

    public Pesquisa getPesquisa(){
        return this.pesquisa;
    }
}
