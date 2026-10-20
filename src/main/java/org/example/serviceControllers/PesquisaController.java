package org.example.serviceControllers;

import org.example.models.researchModels.Pesquisa;
import java.util.ArrayList;
import java.util.List;

public class PesquisaController {
    private List<Pesquisa> pesquisas = new ArrayList<>();

    public void salvar(Pesquisa pesquisa) {
        pesquisas.add(pesquisa);
    }

    public Pesquisa buscarPorCodigo(String codigo) {
        for (Pesquisa p : pesquisas) {
            if (p.getCodigo().equals(codigo)) return p;
        }
        return null;
    }

    public List<Pesquisa> listarTodas() {
        return pesquisas;
    }
}