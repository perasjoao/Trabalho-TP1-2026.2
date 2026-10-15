package org.example.models;

public class Adm extends Usuario{
    Usuario[] usuarios;
    Pesquisa[] pesquisas;
    Partido[] Partidos;
    Candidato[] Candidatos;
    @Override
    public String getCargo() {
        return "Administrador";
    }

}
