package org.example.viewControllers.AdministrationController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.serviceControllers.ParametroController;

import java.io.IOException;
import java.util.List;

public class ConfigurarParametrosController {

    @FXML private TabPane tabPaneCategorias;
    @FXML private ListView<String> listCategorias;
    @FXML private TextField txtCategoria;

    private ParametroController parametroController;

    @FXML
    public void initialize() {
        this.parametroController = new ParametroController();

        tabPaneCategorias.getSelectionModel().selectedItemProperty().addListener((obs, abaAntiga, abaNova) -> {
            if (abaNova != null) {
                atualizarListaDaAba(abaNova.getText());
            }
        });

        Tab abaInicial = tabPaneCategorias.getSelectionModel().getSelectedItem();
        if (abaInicial != null) {
            atualizarListaDaAba(abaInicial.getText());
        }
    }

    @FXML
    public void adicionarCategoria(ActionEvent event) {
        String valor = txtCategoria.getText();
        if (valor == null || valor.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campo Vazio", "Digite um valor antes de adicionar.");
            return;
        }

        Tab abaAtual = tabPaneCategorias.getSelectionModel().getSelectedItem();
        if (abaAtual == null) return;

        switch (abaAtual.getText()) {
            case "Faixa etária":
                parametroController.adicionarFaixaEtaria(valor);
                break;
            case "Escolaridade":
                parametroController.adicionarEscolaridade(valor);
                break;
            case "Região":
                parametroController.adicionarRegiao(valor);
                break;
            case "Faixa de renda":
                parametroController.adicionarFaixaRenda(valor);
                break;
        }

        txtCategoria.clear();
        atualizarListaDaAba(abaAtual.getText());
    }

    @FXML
    public void removerCategoria(ActionEvent event) {
        String selecionado = listCategorias.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um item na lista para remover.");
            return;
        }

        Tab abaAtual = tabPaneCategorias.getSelectionModel().getSelectedItem();
        if (abaAtual == null) return;

        switch (abaAtual.getText()) {
            case "Faixa etária":
                parametroController.removerFaixaEtaria(selecionado);
                break;
            case "Escolaridade":
                parametroController.removerEscolaridade(selecionado);
                break;
            case "Região":
                parametroController.removerRegiao(selecionado);
                break;
            case "Faixa de renda":
                parametroController.removerFaixaRenda(selecionado);
                break;
        }

        atualizarListaDaAba(abaAtual.getText());
    }

    private void atualizarListaDaAba(String nomeAba) {
        List<String> itens;
        switch (nomeAba) {
            case "Faixa etária":
                itens = parametroController.listarFaixasEtarias();
                break;
            case "Escolaridade":
                itens = parametroController.listarEscolaridades();
                break;
            case "Região":
                itens = parametroController.listarRegioes();
                break;
            case "Faixa de renda":
                itens = parametroController.listarFaixasRenda();
                break;
            default:
                itens = List.of();
        }
        listCategorias.setItems(FXCollections.observableArrayList(itens));
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