package org.example.viewControllers.ColetaController;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Toggle;
import javafx.scene.layout.FlowPane;
import org.example.models.coleta.Resposta;
import org.example.models.coleta.RespostaEspecial;
import org.example.models.coleta.RespostaOpcao;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;

import java.util.ArrayList;
import java.util.List;

//perguntas de "Multipla Escolha": caixas de selecao, da pra marcar mais de uma opcao
//"nao sabe"/etc. continuam sendo bolinhas e excluem as caixas (ou marca opcoes, ou marca a especial)
public class CampoMultiplaEscolha extends CampoResposta {

    private final List<CheckBox> caixas = new ArrayList<>();

    public CampoMultiplaEscolha(Pergunta pergunta) {
        super(pergunta);
    }

    @Override
    protected void adicionarEntrada(FlowPane linha) {
        for (OpcaoResposta opcao : pergunta.getOpcoes()) {
            CheckBox caixa = new CheckBox(opcao.getTexto());
            caixa.setUserData(opcao);
            //marcou uma opcao, tira a especial
            caixa.selectedProperty().addListener((obs, antes, marcada) -> {
                if (marcada) grupo.selectToggle(null);
            });
            caixas.add(caixa);
            linha.getChildren().add(caixa);
        }
        //marcou a especial, desmarca todas as opcoes
        grupo.selectedToggleProperty().addListener((obs, antes, agora) -> {
            if (agora != null) caixas.forEach(caixa -> caixa.setSelected(false));
        });
    }

    //uma RespostaOpcao pra cada caixa marcada, todas com o id da mesma pergunta
    @Override
    public List<Resposta> getRespostas() {
        Toggle marcado = grupo.getSelectedToggle();
        if (marcado != null && marcado.getUserData() instanceof String especial) {
            return List.of(new RespostaEspecial(pergunta.getId(), pergunta.getEnunciado(), especial));
        }
        List<Resposta> marcadas = new ArrayList<>();
        for (CheckBox caixa : caixas) {
            if (caixa.isSelected() && caixa.getUserData() instanceof OpcaoResposta opcao) {
                marcadas.add(new RespostaOpcao(pergunta.getId(), pergunta.getEnunciado(), opcao.getTexto()));
            }
        }
        return marcadas;
    }

    //quem pede uma resposta so recebe a primeira marcada
    @Override
    protected Resposta lerEntrada() {
        List<Resposta> marcadas = getRespostas();
        return marcadas.isEmpty() ? null : marcadas.get(0);
    }
}
