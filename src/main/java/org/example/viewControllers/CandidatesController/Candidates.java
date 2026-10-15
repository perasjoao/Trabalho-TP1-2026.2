package org.example.viewControllers.CandidatesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Candidates {
    public void AbrirCadastraCadidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/CadastrarCandidato.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirCadastraPartido(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/CadastrarPartido.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
    public void AbrirConsultaCandidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/ConsultarCandidato.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirGerenciarCandidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/GerenciarCandidato.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirGerenciarPartido(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/GerenciarPartido.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirMenuCandidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/Candidatos.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirMenuPrincipal(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }


    public void AbrirExcluirPartido(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/ExcluirPartido.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirBuscarPartido(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/BuscarPartido.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirAtualizarPartido(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/AtualizarPartido.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirEst(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/Estatisticas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void AbrirAssociaCandidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Candidates/AssociaCandidato.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }







}
