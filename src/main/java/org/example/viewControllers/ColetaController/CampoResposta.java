package org.example.viewControllers.ColetaController;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.models.coleta.Resposta;
import org.example.models.coleta.RespostaEspecial;
import org.example.models.researchModels.Pergunta;

//parte visual de uma pergunta na tela de registro
//toda pergunta tem as opcoes especiais (nao sabe etc.); o resto depende do tipo,
//e cada subclasse monta e le a sua entrada do seu jeito
public abstract class CampoResposta {

    protected final Pergunta pergunta;
    //um grupo so por pergunta: marcar uma opcao desmarca as outras
    protected final ToggleGroup grupo = new ToggleGroup();

    protected CampoResposta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }

    public Node montar(int numero) {
        Label enunciado = new Label(numero + ". " + pergunta.getEnunciado());
        enunciado.setWrapText(true);
        enunciado.getStyleClass().add("label-campo");

        FlowPane linha = new FlowPane(16, 8);
        adicionarEntrada(linha);
        for (String especial : RespostaEspecial.OPCOES) {
            linha.getChildren().add(criarRadio(especial, especial));
        }
        return new VBox(6, enunciado, linha);
    }

    //devolve null se a pergunta ficou sem resposta
    public Resposta getResposta() {
        Toggle marcado = grupo.getSelectedToggle();
        if (marcado != null && marcado.getUserData() instanceof String especial) {
            return new RespostaEspecial(pergunta.getId(), pergunta.getEnunciado(), especial);
        }
        return lerEntrada();
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    protected abstract void adicionarEntrada(FlowPane linha);

    protected abstract Resposta lerEntrada();

    protected RadioButton criarRadio(String texto, Object dado) {
        RadioButton radio = new RadioButton(texto);
        radio.setUserData(dado);
        radio.setToggleGroup(grupo);
        return radio;
    }
}
