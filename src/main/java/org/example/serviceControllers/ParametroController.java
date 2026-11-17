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
            this.parametro.getRegioes().add("Nordeste");
            this.parametro.getRegioes().add("Centro-Oeste");
            this.parametro.getRegioes().add("Sudeste");
            this.parametro.getRegioes().add("Sul");

            this.parametro.getFaixasEtarias().add("16-24 anos");
            this.parametro.getFaixasEtarias().add("25-34 anos");
            this.parametro.getFaixasEtarias().add("35-44 anos");
            this.parametro.getFaixasEtarias().add("45-59 anos");
            this.parametro.getFaixasEtarias().add("60 anos ou mais");

            this.parametro.getEscolaridades().add("Ensino Fundamental");
            this.parametro.getEscolaridades().add("Ensino Médio");
            this.parametro.getEscolaridades().add("Ensino Superior");
            this.parametro.getEscolaridades().add("Pós-graduação");

            this.parametro.getFaixasRenda().add("Até 2 salários mínimos");
            this.parametro.getFaixasRenda().add("De 2 a 5 salários mínimos");
            this.parametro.getFaixasRenda().add("De 5 a 10 salários mínimos");
            this.parametro.getFaixasRenda().add("Acima de 10 salários mínimos");
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

    private boolean contemIgnorandoCase(List<String> lista, String valor) {
        for (String item : lista) {
            if (item.equalsIgnoreCase(valor.trim())) {
                return true;
            }
        }
        return false;
    }

    public void adicionarRegiao(String regiao) {
        if (regiao != null && !regiao.trim().isEmpty() && !contemIgnorandoCase(parametro.getRegioes(), regiao)) {
            parametro.getRegioes().add(regiao.trim());
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

    public void adicionarFaixaEtaria(String faixaEtaria) {
        if (faixaEtaria != null && !faixaEtaria.trim().isEmpty() && !contemIgnorandoCase(parametro.getFaixasEtarias(), faixaEtaria)) {
            parametro.getFaixasEtarias().add(faixaEtaria.trim());
            salvarDados();
        }
    }

    public void removerFaixaEtaria(String faixaEtaria) {
        if (parametro.getFaixasEtarias().remove(faixaEtaria)) {
            salvarDados();
        }
    }

    public List<String> listarFaixasEtarias() {
        return parametro.getFaixasEtarias();
    }

    public void adicionarEscolaridade(String escolaridade) {
        if (escolaridade != null && !escolaridade.trim().isEmpty() && !contemIgnorandoCase(parametro.getEscolaridades(), escolaridade)) {
            parametro.getEscolaridades().add(escolaridade.trim());
            salvarDados();
        }
    }

    public void removerEscolaridade(String escolaridade) {
        if (parametro.getEscolaridades().remove(escolaridade)) {
            salvarDados();
        }
    }

    public List<String> listarEscolaridades() {
        return parametro.getEscolaridades();
    }

    public void adicionarFaixaRenda(String faixaRenda) {
        if (faixaRenda != null && !faixaRenda.trim().isEmpty() && !contemIgnorandoCase(parametro.getFaixasRenda(), faixaRenda)) {
            parametro.getFaixasRenda().add(faixaRenda.trim());
            salvarDados();
        }
    }

    public void removerFaixaRenda(String faixaRenda) {
        if (parametro.getFaixasRenda().remove(faixaRenda)) {
            salvarDados();
        }
    }

    public List<String> listarFaixasRenda() {
        return parametro.getFaixasRenda();
    }
}