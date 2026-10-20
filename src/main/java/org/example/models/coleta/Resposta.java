package org.example.models.coleta;

public class Resposta {
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