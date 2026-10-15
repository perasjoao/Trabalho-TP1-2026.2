package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class ColetaController {

    // volta ao menu do modulo de coleta
    @FXML
    public void voltarParaMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/MenuColeta.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void voltarMenuPrincipal(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void salvarRegistro(ActionEvent event) throws IOException {

        voltarParaMenu(event);
    }

    @FXML
    public void realizarPesquisa(ActionEvent event) throws IOException {
        voltarParaMenu(event);
    }
    @FXML
    public void ProxPesquisa(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/RegistrarRespostas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
    @FXML
    public void ConfigAm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/ConfigAmostra.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
    @FXML
    public void abrirFiltroEntrevistas(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/FiltroEntrevistas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
}