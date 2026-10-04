package org.example.CandidatesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Candidates {
    public void AbrirCadastraCadidato(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Candidates/CadastraUsuarios.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.setScene(new Scene(root));
        tela.show();
    }
}
