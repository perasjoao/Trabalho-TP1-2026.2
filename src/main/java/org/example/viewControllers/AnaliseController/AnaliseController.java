package org.example.viewControllers.AnaliseController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class AnaliseController {

    //funcao para facilitar a troca da tela atual para a do caminho informado
    private void trocarTela(ActionEvent event, String caminhoFxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    //botoes dentro do menu de analise
    @FXML
    public void abrirResultados(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/Resultados.fxml");
    }

    @FXML
    public void abrirProcessarPesquisa(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/ProcessarPesquisa.fxml");
    }

    @FXML
    public void abrirCompararPesquisas(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/CompararPesquisas.fxml");
    }

    @FXML
    public void abrirRelatorios(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/Relatorios.fxml");
    }

    //botao para voltar ao menu de analise
    @FXML
    public void abrirMenuAnalise(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/MenuAnalise.fxml");
    }

    //botao para voltar ao menu principal
    @FXML
    public void voltarMenuPrincipal(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Menu/Menu.fxml");
    }
}
