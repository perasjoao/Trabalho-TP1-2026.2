package org.example.models.coleta;

//resposta digitada, pras perguntas do tipo "Resposta Livre"
public class RespostaLivre extends Resposta {
    private static final long serialVersionUID = 1L;

    public RespostaLivre(String idPergunta, String textoPergunta, String texto) {
        super(idPergunta, textoPergunta, texto.trim());
    }

    @Override
    public String getTipo() {
        return "Livre";
    }

    @Override
    public boolean contaNoPercentual() {
        return true;
    }
}
