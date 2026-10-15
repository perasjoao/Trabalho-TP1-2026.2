package org.example.serviceControllers;

import org.example.models.Pesquisa;
import org.example.models.Usuario;

import java.nio.channels.FileLock;
import java.util.Map;

public class PesquisaController {

    void cadatraPesquisa(String nome, Usuario userlogado){}

    void excluiPesquisa(String nome,Usuario userlogado){}

    Pesquisa[] listaPesquisas(){}

    void mudaStatus(String nome,Usuario userlogado){}

    void mudaRegiao(String nome,Usuario userlogado){}

    Pesquisa[] pesquisasIncompletas(){}

    public Map<String, Float> estatisticaPesquisa(){}

    public void associCandidato(){}


}
