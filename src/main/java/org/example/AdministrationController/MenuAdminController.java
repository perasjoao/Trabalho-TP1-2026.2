package org.example.AdministrationController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuAdminController {

    public void abrirGerenciarUsuarios(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/GerenciarUsuarios.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    public void abrirGerenciarPermissoes(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/GerenciarPermissoes.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    public void abrirMenuPrincipal(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    public void abrirConfigurarParametros(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/ConfigurarParametros.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    public void abrirRelatorioUsuarios(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/RelatorioUsuario.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }
}
