package org.example.administracao.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Um parametro geral e a lista de valores aceitos para ele,
// por exemplo REGIAO -> [Norte, Sul, Centro] (RF-ADM-6)
public class Parametro {

    private final TipoParametro tipo;
    private final List<String> valores = new ArrayList<>();

    public Parametro(TipoParametro tipo) {
        this.tipo = tipo;
    }

    public TipoParametro getTipo() {
        return tipo;
    }

    public List<String> getValores() {
        return Collections.unmodifiableList(valores);
    }

    public void adicionarValor(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("A categoria não pode ser vazia.");
        }
        if (contem(valor)) {
            throw new IllegalArgumentException("A categoria \"" + valor + "\" já existe em " + tipo + ".");
        }
        valores.add(valor.trim());
    }

    public void removerValor(String valor) {
        valores.removeIf(v -> v.equalsIgnoreCase(valor));
    }

    public boolean contem(String valor) {
        return valores.stream().anyMatch(v -> v.equalsIgnoreCase(valor));
    }
}
