package org.example.viewControllers.ResearchController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.PesquisaController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GerenciarPesquisasController {

    // Instância compartilhada do controller de negócio entre as telas de Pesquisa
    private static PesquisaController pesquisaController;

    public static PesquisaController getPesquisaController() {
        if (pesquisaController == null) {
            pesquisaController = new PesquisaController();
        }
        return pesquisaController;
    }

    public static void setPesquisaController(PesquisaController controller) {
        pesquisaController = controller;
    }

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNomeCandidato;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Regiao> cbRegiao;
    @FXML private ComboBox<Cargo> cbCargo;
    @FXML private ComboBox<String> cbSituacao;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;

    @FXML private TableView<Pesquisa> tblPesquisas;
    @FXML private TableColumn<Pesquisa, String> colCodigo;
    @FXML private TableColumn<Pesquisa, String> colNome;
    @FXML private TableColumn<Pesquisa, String> colTipo;
    @FXML private TableColumn<Pesquisa, String> colRegiao;
    @FXML private TableColumn<Pesquisa, String> colCargo;
    @FXML private TableColumn<Pesquisa, String> colSituacao;
    @FXML private TableColumn<Pesquisa, String> colPeriodo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Popula os ComboBoxes com os enums reais
        cbRegiao.setItems(FXCollections.observableArrayList(Regiao.values()));
        cbCargo.setItems(FXCollections.observableArrayList(Cargo.values()));

        // Configura as colunas da TableView
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));

        colCargo.setCellValueFactory(cellData -> {
            Cargo c = cellData.getValue().getCargo();
            return new SimpleStringProperty(c != null ? c.toString() : "");
        });

        colRegiao.setCellValueFactory(cellData -> {
            Pesquisa p = cellData.getValue();
            if (p.getRegioesAssociadas() != null && !p.getRegioesAssociadas().isEmpty()) {
                String regioes = p.getRegioesAssociadas().stream()
                        .map(Regiao::toString)
                        .collect(Collectors.joining(", "));
                return new SimpleStringProperty(regioes);
            }
            return new SimpleStringProperty(p.getAbrangencia() != null ? p.getAbrangencia() : "");
        });

        colPeriodo.setCellValueFactory(cellData -> {
            Pesquisa p = cellData.getValue();
            String ini = p.getDataInicio() != null ? p.getDataInicio().format(formatter) : "?";
            String fim = p.getDataFim() != null ? p.getDataFim().format(formatter) : "?";
            return new SimpleStringProperty(ini + " - " + fim);
        });

        carregarTabela(getPesquisaController().getPesquisas());
    }

    private void carregarTabela(List<Pesquisa> lista) {
        tblPesquisas.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    public void filtrarPesquisas(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String candidato = txtNomeCandidato.getText();
        String tipo = cbTipo.getValue();
        Regiao regiao = cbRegiao.getValue();
        Cargo cargo = cbCargo.getValue();
        String situacao = cbSituacao.getValue();
        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim = dpDataFim.getValue();

        // Aplica o filtro combinado pelo PesquisaController
        List<Pesquisa> filtradas = getPesquisaController().buscarComFiltros(
                candidato, cargo, tipo, situacao, regiao,
                null, inicio, fim, false, new ArrayList<>()
        );

        // Refina pelo código caso o usuário tenha preenchido o campo Código
        if (codigo != null && !codigo.trim().isEmpty()) {
            String codBusca = codigo.trim().toLowerCase();
            filtradas = filtradas.stream()
                    .filter(p -> p.getCodigo() != null && p.getCodigo().toLowerCase().contains(codBusca))
                    .collect(Collectors.toList());
        }

        carregarTabela(filtradas);
    }

    @FXML
    public void limparFiltros(ActionEvent event) {
        txtCodigo.clear();
        txtNomeCandidato.clear();
        cbTipo.setValue(null);
        cbRegiao.setValue(null);
        cbCargo.setValue(null);
        cbSituacao.setValue(null);
        dpDataInicio.setValue(null);
        dpDataFim.setValue(null);

        carregarTabela(getPesquisaController().getPesquisas());
    }

    @FXML
    public void excluirPesquisa(ActionEvent event) {
        Pesquisa selecionada = tblPesquisas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pesquisa na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir pesquisa " + selecionada.getCodigo() + " - " + selecionada.getNome() + "?");
        confirmacao.setContentText("Esta ação não poderá ser desfeita.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                getPesquisaController().deletarPesquisa(selecionada.getCodigo());
                carregarTabela(getPesquisaController().getPesquisas());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Pesquisa excluída com sucesso.");
            } catch (PesquisaInvalidaException | PersistenciaException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao excluir", e.getMessage());
            }
        }
    }

    @FXML
    public void abrirMenuResearch(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Research/MenuResearch.fxml", true);
    }

    @FXML
    public void abrirCadastrarPesquisa(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view.Research/CadastrarPesquisa.fxml"));
        Parent root = loader.load();

        CadastrarPesquisaController controller = loader.getController();
        controller.setPesquisaEdicao(null); // Modo criação

        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    @FXML
    public void abrirEditarPesquisa(ActionEvent event) throws IOException {
        Pesquisa selecionada = tblPesquisas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pesquisa na tabela para editar.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view.Research/CadastrarPesquisa.fxml"));
        Parent root = loader.load();

        CadastrarPesquisaController controller = loader.getController();
        controller.setPesquisaEdicao(selecionada); // Modo edição

        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    @FXML
    public void abrirConfigurarPesquisa(ActionEvent event) throws IOException {
        Pesquisa selecionada = tblPesquisas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pesquisa na tabela para configurar.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view.Research/ConfigurarPesquisa.fxml"));
        Parent root = loader.load();

        ConfigurarPesquisaController controller = loader.getController();
        controller.setPesquisaConfiguracao(selecionada);

        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    private void trocarTela(ActionEvent event, String caminhoFxml, boolean manterTamanho) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (manterTamanho) {
            double width = tela.getWidth();
            double height = tela.getHeight();
            tela.getScene().setRoot(root);
            tela.setWidth(width);
            tela.setHeight(height);
        } else {
            tela.getScene().setRoot(root);
            tela.show();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}