package org.example.serviceControllers;
import org.example.models.Usuario;

import java.util.Map;

public class UsuarioController {
    public boolean cadatraUsuario(String nome, String senha){return true;}

    public boolean excluirUsuario(String nomeExcluir,String senhaUser, Usuario userLogado){}

    public boolean editarUsuario(String nomeExcluir, String senhaUser, Usuario userLogado){}

    public Usuario[] listarUsuarios(String[]parametros){}

    public Map<String,Float> estatisticasUsuarios(){return null;}

    public void adicionaParametroPesquisa(String parametro){}
