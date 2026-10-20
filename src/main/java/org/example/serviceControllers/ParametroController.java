package org.example.serviceControllers;

import org.example.models.Parametro;
import java.util.List;

public class ParametroController {
    private Parametro parametro;

    public ParametroController() {
        this.parametro = new Parametro();
        // Cargas iniciais em memória para teste
        this.parametro.getRegioes().add("Norte");
        this.parametro.getRegioes().add("Sul");
        this.parametro.getRegioes().add("Centro-Oeste");

        this.parametro.getFaixasEtarias().add("18-24 anos");
        this.parametro.getFaixasEtarias().add("25-34 anos");

        this.parametro.getEscolaridades().add("Ensino Médio");
        this.parametro.getEscolaridades().add("Ensino Superior");

        this.parametro.getFaixasRenda().add("Até 2 salários mínimos");
        this.parametro.getFaixasRenda().add("Acima de 2 salários mínimos");
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void adicionarRegiao(String regiao) {
        if (regiao != null && !regiao.trim().isEmpty() && !parametro.getRegioes().contains(regiao)) {
            parametro.getRegioes().add(regiao);
        }
    }

    public void removerRegiao(String regiao) {
        parametro.getRegioes().remove(regiao);
    }

    public List<String> listarRegioes() {
        return parametro.getRegioes();
    }
}