package org.example.viewControllers.ResearchController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciarPesquisasController {

    public void abrirMenuResearch(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Research/MenuResearch.fxml", true);
    }


    public void abrirCadastrarPesquisa(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Research/CadastrarPesquisa.fxml", false);
    }


    public void abrirEditarPesquisa(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Research/CadastrarPesquisa.fxml", false);
    }

    public void abrirConfigurarPesquisa(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Research/ConfigurarPesquisa.fxml", false);
    }


    private void trocarTela(ActionEvent event, String caminhoFxml, boolean manterTamanho) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (manterTamanho) {
            double width = tela.getWidth();
            double height = tela.getHeight();
            tela.getScene().setRoot(root);
            tela.setWidth(width);
            tela.setHeight(height);
        } else {
            tela.getScene().setRoot(root);
            tela.show();
        }
    }
}