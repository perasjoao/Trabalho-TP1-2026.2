package org.example.viewControllers.AdministrationController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.example.exceptions.EmailJaCadastradoException;
import org.example.exceptions.SenhaInvalidaException;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.serviceControllers.UsuarioController;

import java.io.IOException;

public class GerenciarPermissoesController {

    @FXML private ComboBox<Usuario> cbUsuario;
    @FXML private ComboBox<Perfil> cbPerfil;
    @FXML private CheckBox chkAcessoTotal;
    @FXML private CheckBox chkPesquisaColeta;
    @FXML private CheckBox chkResultados;

    private UsuarioController usuarioController;

    @FXML
    public void initialize() {
        this.usuarioController = new UsuarioController();

        cbPerfil.setItems(FXCollections.observableArrayList(Perfil.values()));
        cbUsuario.setItems(FXCollections.observableArrayList(usuarioController.listarUsuarios()));

        cbUsuario.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " (" + item.getEmail() + ")");
            }
        });
        cbUsuario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome() + " (" + item.getEmail() + ")");
            }
        });

        cbUsuario.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null && selecionado.getPerfil() != null) {
                cbPerfil.setValue(selecionado.getPerfil());
                atualizarCheckBoxesPorPerfil(selecionado.getPerfil());
            }
        });

        cbPerfil.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novoPerfil) -> {
            if (novoPerfil != null) {
                atualizarCheckBoxesPorPerfil(novoPerfil);
            }
        });

        chkAcessoTotal.setOnAction(e -> {
            if (chkAcessoTotal.isSelected()) {
                cbPerfil.setValue(Perfil.ADMINISTRADOR);
            }
        });
        chkPesquisaColeta.setOnAction(e -> {
            if (chkPesquisaColeta.isSelected() && !chkAcessoTotal.isSelected()) {
                cbPerfil.setValue(Perfil.PESQUISADOR);
            }
        });
        chkResultados.setOnAction(e -> {
            if (chkResultados.isSelected() && !chkAcessoTotal.isSelected()) {
                cbPerfil.setValue(Perfil.ANALISTA);
            }
        });
    }

    private void atualizarCheckBoxesPorPerfil(Perfil perfil) {
        switch (perfil) {
            case ADMINISTRADOR:
                chkAcessoTotal.setSelected(true);
                chkPesquisaColeta.setSelected(true);
                chkResultados.setSelected(true);
                break;
            case PESQUISADOR:
                chkAcessoTotal.setSelected(false);
                chkPesquisaColeta.setSelected(true);
                chkResultados.setSelected(false);
                break;
            case ANALISTA:
                chkAcessoTotal.setSelected(false);
                chkPesquisaColeta.setSelected(false);
                chkResultados.setSelected(true);
                break;
        }
    }

    @FXML
    public void salvarPermissoes(ActionEvent event) {
        Usuario usuarioSelecionado = cbUsuario.getValue();
        Perfil perfilSelecionado = cbPerfil.getValue();

        if (usuarioSelecionado == null || perfilSelecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um usuário e um perfil antes de salvar.");
            return;
        }

        try {
            Usuario dadosAtualizados = new Usuario(
                    usuarioSelecionado.getNome(),
                    usuarioSelecionado.getEmail(),
                    null,
                    perfilSelecionado,
                    usuarioSelecionado.isStatus()
            );

            usuarioController.editarUsuario(usuarioSelecionado.getEmail(), dadosAtualizados);
            cbUsuario.setItems(FXCollections.observableArrayList(usuarioController.listarUsuarios()));
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Perfil e permissões de " + usuarioSelecionado.getNome() + " atualizados para " + perfilSelecionado + "!");
        } catch (EmailJaCadastradoException | SenhaInvalidaException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void voltarParaMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Administration/MenuAdmin.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }
}