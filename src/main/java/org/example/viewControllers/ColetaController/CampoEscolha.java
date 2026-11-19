package org.example.viewControllers.ColetaController;

import javafx.scene.control.Toggle;
import javafx.scene.layout.FlowPane;
import org.example.models.coleta.Resposta;
import org.example.models.coleta.RespostaOpcao;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;

//perguntas com opcoes cadastradas (unica escolha, multipla escolha, escala)
public class CampoEscolha extends CampoResposta {

    public CampoEscolha(Pergunta pergunta) {
        super(pergunta);
    }

    @Override
    protected void adicionarEntrada(FlowPane linha) {
        for (OpcaoResposta opcao : pergunta.getOpcoes()) {
            linha.getChildren().add(criarRadio(opcao.getTexto(), opcao));
        }
    }

    @Override
    protected Resposta lerEntrada() {
        Toggle marcado = grupo.getSelectedToggle();
        if (marcado != null && marcado.getUserData() instanceof OpcaoResposta opcao) {
            return new RespostaOpcao(pergunta.getId(), pergunta.getEnunciado(), opcao.getTexto());
        }
        return null;
    }
}
