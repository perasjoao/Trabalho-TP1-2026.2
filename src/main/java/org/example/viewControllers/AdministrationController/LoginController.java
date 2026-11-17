package org.example.viewControllers.AdministrationController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.exceptions.CredenciaisInvalidasException;
import org.example.exceptions.UsuarioInativoException;
import org.example.utils.Sessao;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label lblErro;

    @FXML
    public void entrar(ActionEvent event) throws IOException {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String senha = txtSenha.getText() == null ? "" : txtSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            lblErro.setText("Informe o e-mail e a senha.");
            return;
        }

        try {
            Sessao.getAutenticacao().autenticar(email, senha);
        } catch (CredenciaisInvalidasException | UsuarioInativoException e) {
            lblErro.setText(e.getMessage());
            txtSenha.clear();
            return;
        }

        // so chega aqui se a autenticacao deu certo
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
}
