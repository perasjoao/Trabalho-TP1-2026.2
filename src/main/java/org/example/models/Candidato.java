package org.example.models;

public class Candidato{
    private Partido partido;
    private int numero;
    String situacao;
    String cargo;

    @Override
    public String getCargo(){
        return "Candidado";
    }

    public Candidato(String nome,Partido partido,int numero){
        this.nome=nome;
        this.partido=partido;
        this.numero=numero;
    }

    public int getNumero() {
        return numero;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }
}
