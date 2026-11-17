package org.example.viewControllers.AdministrationController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuAdminController {

    public void abrirGerenciarUsuarios(ActionEvent event) throws IOException {
        trocarTela(event, "/view/Administration/GerenciarUsuarios.fxml");
    }

    public void abrirGerenciarPermissoes(ActionEvent event) throws IOException {
        trocarTela(event, "/view/Administration/GerenciarPermissoes.fxml");
    }

    public void abrirMenuPrincipal(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Menu/Menu.fxml");
    }

    public void abrirConfigurarParametros(ActionEvent event) throws IOException {
        trocarTela(event, "/view/Administration/ConfigurarParametros.fxml");
    }

    public void abrirRelatorioUsuarios(ActionEvent event) throws IOException {
        trocarTela(event, "/view/Administration/RelatorioUsuario.fxml");
    }

    private void trocarTela(ActionEvent event, String caminhoFxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
}