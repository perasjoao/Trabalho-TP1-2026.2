package org.example.administracao.aplicacao;

import org.example.administracao.dominio.Parametro;
import org.example.administracao.dominio.TipoParametro;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Caso de uso de configuracao dos parametros gerais (RF-ADM-6)
public class ParametroControlador {

    private final Map<TipoParametro, Parametro> parametros = new EnumMap<>(TipoParametro.class);

    public ParametroControlador() {
        // cada tipo comeca com a lista vazia
        for (TipoParametro tipo : TipoParametro.values()) {
            parametros.put(tipo, new Parametro(tipo));
        }
    }

    public void adicionarCategoria(TipoParametro tipo, String valor) {
        parametros.get(tipo).adicionarValor(valor);
    }

    public void removerCategoria(TipoParametro tipo, String valor) {
        parametros.get(tipo).removerValor(valor);
    }

    public List<String> listarCategorias(TipoParametro tipo) {
        return parametros.get(tipo).getValores();
    }

    // usado pela coleta para conferir as caracteristicas do participante (RN-COL-4)
    public boolean categoriaValida(TipoParametro tipo, String valor) {
        return parametros.get(tipo).contem(valor);
    }
}
