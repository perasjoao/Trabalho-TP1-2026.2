package org.example.viewControllers.AdministrationController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciarPermissoesController {

    public void voltarParaMenu(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/MenuAdmin.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }
}
