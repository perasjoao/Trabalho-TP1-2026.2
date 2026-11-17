package org.example.viewControllers.ResearchController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.utils.Sessao;

import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuReaserchController implements Initializable{

    @FXML private Button botaoGerenciar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario usrLogado= Sessao.getUsuarioLogado();
        if(usrLogado.getPerfil()== Perfil.ANALISTA){
            botaoGerenciar.setVisible(false);
        }
    }


    public void abrirGerenciarPesquisas(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/GerenciarPesquisa.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void abrirConsultarPesquisas(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/ConsultarPesquisas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    public void abrirMenuPrincipal(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

}
