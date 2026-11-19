package org.example.viewControllers.ColetaController;

import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.example.models.coleta.Resposta;
import org.example.models.coleta.RespostaLivre;
import org.example.models.researchModels.Pergunta;

//perguntas do tipo "Resposta Livre": campo de texto + as opcoes especiais
public class CampoTextoLivre extends CampoResposta {

    private final TextField campo = new TextField();

    public CampoTextoLivre(Pergunta pergunta) {
        super(pergunta);
    }

    @Override
    protected void adicionarEntrada(FlowPane linha) {
        campo.setPromptText("Digite a resposta");
        campo.setPrefWidth(280);
        campo.getStyleClass().add("campo-texto");

        //ou digita, ou marca "nao sabe"/etc., nunca os dois
        campo.textProperty().addListener((obs, antes, agora) -> {
            if (agora != null && !agora.isBlank()) grupo.selectToggle(null);
        });
        grupo.selectedToggleProperty().addListener((obs, antes, agora) -> {
            if (agora != null) campo.clear();
        });
        linha.getChildren().add(campo);
    }

    @Override
    protected Resposta lerEntrada() {
        String texto = campo.getText();
        if (texto == null || texto.isBlank()) return null;
        return new RespostaLivre(pergunta.getId(), pergunta.getEnunciado(), texto);
    }
}
