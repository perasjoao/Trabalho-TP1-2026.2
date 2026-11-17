package org.example.viewControllers.AdministrationController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.models.Perfil;
import org.example.models.Usuario;
import org.example.serviceControllers.UsuarioController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RelatorioUsuarioController {

    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblAtivos;
    @FXML private Label lblInativos;
    @FXML private Label lblAdmins;
    @FXML private Label lblPesquisadores;
    @FXML private Label lblAnalistas;
    @FXML private ComboBox<String> cbFiltroPerfil;

    private UsuarioController usuarioController;

    @FXML
    public void initialize() {
        this.usuarioController = new UsuarioController();

        cbFiltroPerfil.getItems().add("Todos");
        for (Perfil p : Perfil.values()) {
            cbFiltroPerfil.getItems().add(p.toString());
        }

        cbFiltroPerfil.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            atualizarIndicadores(selecionado);
        });

        atualizarIndicadores("Todos");
    }

    private void atualizarIndicadores(String perfilFiltro) {
        Perfil perfilSelecionado = null;
        if (perfilFiltro != null && !perfilFiltro.equals("Todos")) {
            for (Perfil p : Perfil.values()) {
                if (p.toString().equalsIgnoreCase(perfilFiltro) || p.name().equalsIgnoreCase(perfilFiltro)) {
                    perfilSelecionado = p;
                    break;
                }
            }
        }

        List<Usuario> listaFiltrada = usuarioController.filtrarUsuarios(null, perfilSelecionado, null);
        int total = listaFiltrada.size();
        int ativos = (int) listaFiltrada.stream().filter(Usuario::isStatus).count();
        int inativos = total - ativos;

        lblTotalUsuarios.setText(String.valueOf(total));
        lblAtivos.setText(String.valueOf(ativos));
        lblInativos.setText(String.valueOf(inativos));

        lblAdmins.setText("Administradores: " + usuarioController.contarPorPerfil(Perfil.ADMINISTRADOR));
        lblPesquisadores.setText("Pesquisadores: " + usuarioController.contarPorPerfil(Perfil.PESQUISADOR));
        lblAnalistas.setText("Analistas: " + usuarioController.contarPorPerfil(Perfil.ANALISTA));
    }

    @FXML
    public void exportarRelatorio(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Usuários");
        fileChooser.setInitialFileName("relatorio_usuarios.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivo = fileChooser.showSaveDialog(stage);

        if (arquivo != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
                writer.println("RESUMO GERAL");
                writer.println("Total de Usuarios;" + usuarioController.contarTotal());
                writer.println("Ativos;" + usuarioController.contarAtivos());
                writer.println("Inativos;" + usuarioController.contarInativos());
                writer.println("Administradores;" + usuarioController.contarPorPerfil(Perfil.ADMINISTRADOR));
                writer.println("Pesquisadores;" + usuarioController.contarPorPerfil(Perfil.PESQUISADOR));
                writer.println("Analistas;" + usuarioController.contarPorPerfil(Perfil.ANALISTA));
                writer.println();
                writer.println("LISTA DE USUARIOS");
                writer.println("Nome;Email;Perfil;Status");

                for (Usuario u : usuarioController.listarUsuarios()) {
                    writer.println(
                            u.getNome() + ";" +
                                    u.getEmail() + ";" +
                                    (u.getPerfil() != null ? u.getPerfil().toString() : "") + ";" +
                                    (u.isStatus() ? "Ativo" : "Inativo")
                    );
                }

                exibirAlerta(Alert.AlertType.INFORMATION, "Exportação Concluída",
                        "Relatório salvo com sucesso em:\n" + arquivo.getAbsolutePath());
            } catch (IOException e) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro ao Exportar", "Não foi possível salvar o arquivo: " + e.getMessage());
            }
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