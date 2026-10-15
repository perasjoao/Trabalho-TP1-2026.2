package org.example.models;

public class Entrevistador extends Usuario{
    String nome;
    int codigo; //id

    @Override
    public String getCargo() {
        return "Entrevistador";
    }
}
