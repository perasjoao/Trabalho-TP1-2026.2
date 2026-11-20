package org.example.models.researchModels;

import org.example.exceptions.PesquisaInvalidaException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlanoAmostral {
    private String descricaoMetodologia;
    private Map<String, Integer> metasPorGrupo;
    private Map<String, Map<String, Integer>> metasPorCategoria;

    public PlanoAmostral(String descricaoMetodologia) {
        this.descricaoMetodologia = descricaoMetodologia;
        this.metasPorGrupo = new LinkedHashMap<>();
        this.metasPorCategoria = new LinkedHashMap<>();
    }

    public void definirMeta(String categoria, String grupo, int quantidadeMinima) {
        if (categoria == null || categoria.trim().isEmpty() || grupo == null || grupo.trim().isEmpty()) {
            return;
        }
        String cat = categoria.trim();
        String grp = grupo.trim();

        if (quantidadeMinima <= 0) {
            removerMeta(cat, grp);
            return;
        }

        metasPorCategoria.computeIfAbsent(cat, k -> new LinkedHashMap<>()).put(grp, quantidadeMinima);
        metasPorGrupo.put(grp, quantidadeMinima);
    }

    public void removerMeta(String categoria, String grupo) {
        if (categoria != null && metasPorCategoria.containsKey(categoria)) {
            metasPorCategoria.get(categoria).remove(grupo);
            if (metasPorCategoria.get(categoria).isEmpty()) {
                metasPorCategoria.remove(categoria);
            }
        }
        if (grupo != null) {
            metasPorGrupo.remove(grupo);
        }
    }

    public int getSomaCategoria(String categoria) {
        Map<String, Integer> grupos = metasPorCategoria.get(categoria);
        if (grupos == null) return 0;
        return grupos.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void validarSomasPorCategoria(int entrevistasPrevistas) throws PesquisaInvalidaException {
        for (Map.Entry<String, Map<String, Integer>> entry : metasPorCategoria.entrySet()) {
            int soma = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            if (soma > entrevistasPrevistas) {
                throw new PesquisaInvalidaException(
                        "A soma das metas da categoria '" + entry.getKey() + "' (" + soma +
                                ") não pode ultrapassar o total de entrevistas previstas (" + entrevistasPrevistas + ")."
                );
            }
        }
    }

    public boolean atingiuMetaTodosGrupos(Map<String, Integer> realizadasPorGrupo) {
        if (!metasPorCategoria.isEmpty()) {
            for (Map<String, Integer> grupos : metasPorCategoria.values()) {
                for (Map.Entry<String, Integer> meta : grupos.entrySet()) {
                    int realizadas = realizadasPorGrupo.getOrDefault(meta.getKey(), 0);
                    if (realizadas < meta.getValue()) {
                        return false;
                    }
                }
            }
            return true;
        }

        for (Map.Entry<String, Integer> meta : metasPorGrupo.entrySet()) {
            int realizadas = realizadasPorGrupo.getOrDefault(meta.getKey(), 0);
            if (realizadas < meta.getValue()) {
                return false;
            }
        }
        return true;
    }

    public String getDescricaoMetodologia() { return descricaoMetodologia; }
    public void setDescricaoMetodologia(String descricaoMetodologia) { this.descricaoMetodologia = descricaoMetodologia; }

    public Map<String, Integer> getMetasPorGrupo() { return metasPorGrupo; }

    public Map<String, Map<String, Integer>> getMetasPorCategoria() {
        return Collections.unmodifiableMap(metasPorCategoria);
    }
}