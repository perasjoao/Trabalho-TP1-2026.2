package org.example.models.coleta;

import java.io.Serializable;

public class Resposta implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idPergunta;
    private String textoPergunta;
    private String opcaoEscolhida;

    public Resposta() {
    }

    public Resposta(String idPergunta, String textoPergunta, String opcaoEscolhida) {
        this.idPergunta = idPergunta;
        this.textoPergunta = textoPergunta;
        this.opcaoEscolhida = opcaoEscolhida;
    }

    public String getIdPergunta() { return idPergunta; }
    public void setIdPergunta(String idPergunta) { this.idPergunta = idPergunta; }

    public String getTextoPergunta() { return textoPergunta; }
    public void setTextoPergunta(String textoPergunta) { this.textoPergunta = textoPergunta; }

    public String getOpcaoEscolhida() { return opcaoEscolhida; }
    public void setOpcaoEscolhida(String opcaoEscolhida) { this.opcaoEscolhida = opcaoEscolhida; }
}