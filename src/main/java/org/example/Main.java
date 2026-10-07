package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public void start(Stage telaPrincipal) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));

        telaPrincipal.setTitle("Sistema de Pesquisas Eleitorais");
        telaPrincipal.setScene(new Scene(root));
        // sempre que uma tela for trocada (setRoot), ajusta a janela ao tamanho da nova tela
        telaPrincipal.getScene().rootProperty().addListener((obs, telaAntiga, telaNova) -> telaPrincipal.sizeToScene());
        telaPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}