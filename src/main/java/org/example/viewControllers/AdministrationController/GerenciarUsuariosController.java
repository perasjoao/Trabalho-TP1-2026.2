package org.example.viewControllers.AdministrationController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.exceptions.EmailJaCadastradoException;
import org.example.exceptions.SenhaInvalidaException;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.serviceControllers.UsuarioController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GerenciarUsuariosController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<Perfil> cbPerfil;
    @FXML private ComboBox<String> cbStatus;

    @FXML private TextField txtBuscaNome;
    @FXML private ComboBox<String> cbFiltroPerfil;
    @FXML private ComboBox<String> cbFiltroStatus;

    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colPerfil;
    @FXML private TableColumn<Usuario, String> colStatus;

    private UsuarioController usuarioController;
    private String emailSelecionado;

    @FXML
    public void initialize() {
        this.usuarioController = new UsuarioController();

        cbPerfil.setItems(FXCollections.observableArrayList(Perfil.values()));

        cbFiltroPerfil.getItems().add("Todos");
        for (Perfil p : Perfil.values()) {
            cbFiltroPerfil.getItems().add(p.toString());
        }

        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPerfil.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPerfil() != null ? cellData.getValue().getPerfil().toString() : ""));
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Ativo" : "Inativo"));

        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });

        atualizarTabela(usuarioController.listarUsuarios());
    }

    @FXML
    public void salvarUsuario(ActionEvent event) {
        try {
            if (cbStatus.getValue() == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Selecione o status do usuário (Ativo ou Inativo).");
                return;
            }

            boolean ativo = cbStatus.getValue().equalsIgnoreCase("Ativo");
            Usuario novoUsuario = new Usuario(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    cbPerfil.getValue(),
                    ativo
            );

            usuarioController.cadastrarUsuario(novoUsuario);
            atualizarTabela(usuarioController.listarUsuarios());
            limparFormulario();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário cadastrado e salvo com sucesso!");

        } catch (EmailJaCadastradoException | SenhaInvalidaException | IllegalArgumentException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        }
    }

    @FXML
    public void editarUsuario(ActionEvent event) {
        if (emailSelecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um usuário na tabela antes de editar.");
            return;
        }

        try {
            if (cbStatus.getValue() == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Selecione o status do usuário.");
                return;
            }

            boolean ativo = cbStatus.getValue().equalsIgnoreCase("Ativo");
            Usuario dadosAtualizados = new Usuario(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtSenha.getText(),
                    cbPerfil.getValue(),
                    ativo
            );

            usuarioController.editarUsuario(emailSelecionado, dadosAtualizados);
            atualizarTabela(usuarioController.listarUsuarios());
            limparFormulario();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados do usuário atualizados com sucesso!");

        } catch (EmailJaCadastradoException | SenhaInvalidaException | IllegalArgumentException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao Editar", e.getMessage());
        }
    }

    @FXML
    public void excluirUsuario(ActionEvent event) {
        Usuario selecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um usuário na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir conta definitivamente?");
        confirmacao.setContentText("Deseja realmente excluir o usuário " + selecionado.getNome() + " (" + selecionado.getEmail() + ")?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (usuarioController.excluirUsuario(selecionado.getEmail())) {
                atualizarTabela(usuarioController.listarUsuarios());
                limparFormulario();
                exibirAlerta(Alert.AlertType.INFORMATION, "Excluído", "Usuário removido com sucesso.");
            }
        }
    }

    @FXML
    public void buscarUsuarios(ActionEvent event) {
        String nome = txtBuscaNome.getText();

        Perfil perfilFiltro = null;
        String perfilSel = cbFiltroPerfil.getValue();
        if (perfilSel != null && !perfilSel.equals("Todos")) {
            for (Perfil p : Perfil.values()) {
                if (p.toString().equalsIgnoreCase(perfilSel) || p.name().equalsIgnoreCase(perfilSel)) {
                    perfilFiltro = p;
                    break;
                }
            }
        }

        Boolean statusFiltro = null;
        String statusSel = cbFiltroStatus.getValue();
        if (statusSel != null && !statusSel.equals("Todos")) {
            statusFiltro = statusSel.equalsIgnoreCase("Ativo");
        }

        List<Usuario> filtrados = usuarioController.filtrarUsuarios(nome, perfilFiltro, statusFiltro);
        atualizarTabela(filtrados);
    }

    private void preencherFormulario(Usuario usuario) {
        this.emailSelecionado = usuario.getEmail();
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        txtSenha.clear();
        cbPerfil.setValue(usuario.getPerfil());
        cbStatus.setValue(usuario.isStatus() ? "Ativo" : "Inativo");
    }

    private void limparFormulario() {
        this.emailSelecionado = null;
        txtNome.clear();
        txtEmail.clear();
        txtSenha.clear();
        cbPerfil.setValue(null);
        cbStatus.setValue(null);
        tabelaUsuarios.getSelectionModel().clearSelection();
    }

    private void atualizarTabela(List<Usuario> lista) {
        tabelaUsuarios.setItems(FXCollections.observableArrayList(lista));
        tabelaUsuarios.refresh();
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