package org.example.serviceControllers;

import org.example.models.Parametro;
import org.example.utils.GerenciadorArquivo;

import java.util.ArrayList;
import java.util.List;

public class ParametroController {
    private Parametro parametro;
    private GerenciadorArquivo<Parametro> gerenciador;
    private final String ARQUIVO_PARAMETROS = "parametros.dat";

    public ParametroController() {
        this.gerenciador = new GerenciadorArquivo<>();
        List<Parametro> lista = gerenciador.ler(ARQUIVO_PARAMETROS);

        if (lista != null && !lista.isEmpty()) {
            this.parametro = lista.get(0);
        } else {
            this.parametro = new Parametro();
            this.parametro.getRegioes().add("Norte");
            this.parametro.getRegioes().add("Sul");
            this.parametro.getRegioes().add("Centro-Oeste");

            this.parametro.getFaixasEtarias().add("18-24 anos");
            this.parametro.getFaixasEtarias().add("25-34 anos");

            this.parametro.getEscolaridades().add("Ensino Médio");
            this.parametro.getEscolaridades().add("Ensino Superior");

            this.parametro.getFaixasRenda().add("Até 2 salários mínimos");
            this.parametro.getFaixasRenda().add("Acima de 2 salários mínimos");
            salvarDados();
        }
    }

    private void salvarDados() {
        List<Parametro> lista = new ArrayList<>();
        lista.add(this.parametro);
        gerenciador.salvar(ARQUIVO_PARAMETROS, lista);
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void adicionarRegiao(String regiao) {
        if (regiao != null && !regiao.trim().isEmpty() && !parametro.getRegioes().contains(regiao)) {
            parametro.getRegioes().add(regiao);
            salvarDados();
        }
    }

    public void removerRegiao(String regiao) {
        if (parametro.getRegioes().remove(regiao)) {
            salvarDados();
        }
    }

    public List<String> listarRegioes() {
        return parametro.getRegioes();
    }
}