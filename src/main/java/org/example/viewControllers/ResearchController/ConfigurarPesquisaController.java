package org.example.viewControllers.ResearchController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfigurarPesquisaController {

    public void voltarParaGerenciar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/GerenciarPesquisa.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }
}