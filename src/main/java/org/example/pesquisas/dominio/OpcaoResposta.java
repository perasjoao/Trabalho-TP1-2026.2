package org.example.pesquisas.dominio;

import org.example.candidatos.dominio.Candidato;

// Uma opcao de resposta de uma pergunta. Pode estar ligada a um candidato
// (perguntas de intencao de voto) ou ser apenas um texto ("Sim", "Não"...).
public class OpcaoResposta {

    private final String texto;
    private final Candidato candidato;

    public OpcaoResposta(String texto) {
        this(texto, null);
    }

    public OpcaoResposta(String texto, Candidato candidato) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("O texto da opção é obrigatório.");
        }
        this.texto = texto;
        this.candidato = candidato;
    }

    public String getTexto() {
        return texto;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public boolean isCandidato() {
        return candidato != null;
    }

    @Override
    public String toString() {
        return texto;
    }
}
