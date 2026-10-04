package org.example.MenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Menu {
    public void AbrirAdm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/Login.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.setScene(new Scene(root));
        tela.show();
    }

}
