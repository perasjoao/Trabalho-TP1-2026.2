package org.example.models.researchModels;

import java.util.HashMap;
import java.util.Map;

public class PlanoAmostral {
    private String descricaoMetodologia;
    private Map<String, Integer> metasPorGrupo;

    public PlanoAmostral(String descricaoMetodologia) {
        this.descricaoMetodologia = descricaoMetodologia;
        this.metasPorGrupo = new HashMap<>();
    }

    public void definirMetaGrupo(String grupo, int quantidadeMinima) {
        this.metasPorGrupo.put(grupo, quantidadeMinima);
    }

    public boolean atingiuMeta(Map<String, Integer> realizadasPorGrupo) {
        for (Map.Entry<String, Integer> meta : metasPorGrupo.entrySet()) {
            int realizadas = realizadasPorGrupo.getOrDefault(meta.getKey(), 0);
            if (realizadas < meta.getValue()) {
                return false; // Não atingiu a meta mínima
            }
        }
        return true;
    }

    public String getDescricaoMetodologia() { return descricaoMetodologia; }
    public void setDescricaoMetodologia(String descricaoMetodologia) { this.descricaoMetodologia = descricaoMetodologia; }
    public Map<String, Integer> getMetasPorGrupo() { return metasPorGrupo; }
}