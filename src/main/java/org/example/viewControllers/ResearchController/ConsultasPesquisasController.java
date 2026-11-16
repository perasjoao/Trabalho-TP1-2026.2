package org.example.viewControllers.ResearchController;

import javafx.beans.property.SimpleIntegerProperty;
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
import org.example.models.Entrevista;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;
import org.example.models.researchModels.Pesquisa;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultasPesquisasController {

    // Lista compartilhada com o módulo de Coleta para sincronizar entrevistas realizadas e metas
    private static List<Entrevista> entrevistasDoSistema = new ArrayList<>();

    public static void setEntrevistasDoSistema(List<Entrevista> entrevistas) {
        entrevistasDoSistema = (entrevistas != null) ? entrevistas : new ArrayList<>();
        GerenciarPesquisasController.getPesquisaController().sincronizarEntrevistasRealizadas(entrevistasDoSistema);
    }

    public static List<Entrevista> getEntrevistasDoSistema() {
        return entrevistasDoSistema;
    }

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNomeCandidato;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<Regiao> cbRegiao;
    @FXML private ComboBox<Cargo> cbCargo;
    @FXML private ComboBox<String> cbSituacao;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;
    @FXML private TextField txtQtdEntrevistados;
    @FXML private CheckBox chkAbaixoMeta;

    @FXML private TableView<Pesquisa> tblPesquisas;
    @FXML private TableColumn<Pesquisa, String> colCodigo;
    @FXML private TableColumn<Pesquisa, String> colNome;
    @FXML private TableColumn<Pesquisa, String> colTipo;
    @FXML private TableColumn<Pesquisa, String> colRegiao;
    @FXML private TableColumn<Pesquisa, String> colCargo;
    @FXML private TableColumn<Pesquisa, String> colSituacao;
    @FXML private TableColumn<Pesquisa, String> colPeriodo;
    @FXML private TableColumn<Pesquisa, Number> colPrevisto;
    @FXML private TableColumn<Pesquisa, Number> colRealizado;
    @FXML private TableColumn<Pesquisa, String> colPercentual;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        cbRegiao.setItems(FXCollections.observableArrayList(Regiao.values()));
        cbCargo.setItems(FXCollections.observableArrayList(Cargo.values()));

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

        colPrevisto.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getEntrevistasPrevistas()));

        colRealizado.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getEntrevistasRealizadas()));

        colPercentual.setCellValueFactory(cellData -> {
            Pesquisa p = cellData.getValue();
            int previstas = p.getEntrevistasPrevistas();
            int realizadas = p.getEntrevistasRealizadas();
            double pct = (previstas > 0) ? ((double) realizadas / previstas) * 100.0 : 0.0;
            return new SimpleStringProperty(String.format("%.1f%%", pct));
        });

        GerenciarPesquisasController.getPesquisaController().sincronizarEntrevistasRealizadas(entrevistasDoSistema);
        carregarTabela(GerenciarPesquisasController.getPesquisaController().getPesquisas());
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
        boolean apenasAbaixoMeta = chkAbaixoMeta.isSelected();

        Integer qtdMinEntrevistados = null;
        String qtdTexto = txtQtdEntrevistados.getText();
        if (qtdTexto != null && !qtdTexto.trim().isEmpty()) {
            try {
                qtdMinEntrevistados = Integer.parseInt(qtdTexto.trim());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Filtro inválido",
                        "Informe um número inteiro válido para a quantidade de entrevistados.");
                return;
            }
        }

        List<Pesquisa> filtradas = GerenciarPesquisasController.getPesquisaController().buscarComFiltros(
                candidato, cargo, tipo, situacao, regiao,
                qtdMinEntrevistados, inicio, fim, apenasAbaixoMeta, entrevistasDoSistema
        );

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
        txtQtdEntrevistados.clear();
        chkAbaixoMeta.setSelected(false);

        GerenciarPesquisasController.getPesquisaController().sincronizarEntrevistasRealizadas(entrevistasDoSistema);
        carregarTabela(GerenciarPesquisasController.getPesquisaController().getPesquisas());
    }

    @FXML
    public void abrirMenuResearch(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/MenuResearch.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}