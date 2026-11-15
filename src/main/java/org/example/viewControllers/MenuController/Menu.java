package org.example.viewControllers.MenuController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.utils.Sessao;

import java.io.IOException;

public class Menu {
    public void AbrirAdm(ActionEvent event) throws IOException {
        // o login agora acontece na abertura do sistema; aqui so checa se o usuario e administrador
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario == null || usuario.getPerfil() != Perfil.ADMINISTRADOR) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Acesso negado");
            alerta.setHeaderText(null);
            alerta.setContentText("Apenas administradores podem acessar este módulo.");
            alerta.showAndWait();
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/MenuAdmin.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);

    }

    public void AbrirCandidatos(ActionEvent event) throws IOException {
        //candidatos e partidos: so administrador e pesquisador (o analista nao mantem cadastros)
        Usuario usuario = Sessao.getUsuarioLogado();
        boolean permitido = usuario != null
                && (usuario.getPerfil() == Perfil.ADMINISTRADOR || usuario.getPerfil() == Perfil.PESQUISADOR);
        if (!permitido) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Acesso negado");
            alerta.setHeaderText(null);
            alerta.setContentText("Apenas administradores e pesquisadores podem acessar este módulo.");
            alerta.showAndWait();
            return;
        }
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
