package org.example.viewControllers.MenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {
    public void AbrirAdm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/Login.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }

    public void AbrirCandidatos(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/Candidatos.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }
    public void AbrirPesquisas(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/MenuResearch.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);

        tela.setWidth(width);
        tela.setHeight(height);

    }

    public void AbrirColeta(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/MenuColeta.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }

    public void AbrirAnalise(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Analise/MenuAnalise.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }

}
