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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.PesquisaController;
import org.example.serviceControllers.candidatos.CandidatoController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GerenciarPesquisasController {

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

    @FXML private CheckBox chkTipoIntencao;
    @FXML private CheckBox chkTipoConhecimento;
    @FXML private CheckBox chkTipoRejeicao;
    @FXML private CheckBox chkTipoSegundoTurno;

    @FXML private VBox vboxFiltroRegioes;

    @FXML private CheckBox chkCargoPresidente;
    @FXML private CheckBox chkCargoGovernador;
    @FXML private CheckBox chkCargoSenador;
    @FXML private CheckBox chkCargoDepFederal;
    @FXML private CheckBox chkCargoDepEstadual;

    @FXML private CheckBox chkSituacaoPlanejada;
    @FXML private CheckBox chkSituacaoEmColeta;
    @FXML private CheckBox chkSituacaoEncerrada;
    @FXML private CheckBox chkSituacaoProcessada;

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
    private CheckBox chkTodasRegioes;
    private final List<CheckBox> checkBoxesRegioes = new ArrayList<>();
    private boolean atualizandoCheckBoxesRegiao = false;

    @FXML
    public void initialize() {
        configurarCheckBoxesRegiao();

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
                return new SimpleStringProperty(String.join(", ", p.getRegioesAssociadas()));
            }
            return new SimpleStringProperty(p.getAbrangencia() != null ? p.getAbrangencia() : "");
        });

        colPeriodo.setCellValueFactory(cellData -> {
            Pesquisa p = cellData.getValue();
            String ini = p.getDataInicio() != null ? p.getDataInicio().format(formatter) : "?";
            String fim = p.getDataFim() != null ? p.getDataFim().format(formatter) : "?";
            return new SimpleStringProperty(ini + " - " + fim);
        });

        getPesquisaController().vincularCandidaturasCarregadas(new CandidatoController().listar());
        carregarTabela(getPesquisaController().getPesquisas());
    }

    private void configurarCheckBoxesRegiao() {
        vboxFiltroRegioes.getChildren().clear();
        checkBoxesRegioes.clear();

        List<String> regioesCadastradas = new ParametroController().listarRegioes();

        chkTodasRegioes = new CheckBox("Selecionar todas");
        chkTodasRegioes.setStyle("-fx-font-weight: bold;");
        chkTodasRegioes.selectedProperty().addListener((obs, estavaMarcado, marcadoAgora) -> {
            if (atualizandoCheckBoxesRegiao) return;
            atualizandoCheckBoxesRegiao = true;
            for (CheckBox chk : checkBoxesRegioes) {
                chk.setSelected(marcadoAgora);
            }
            atualizandoCheckBoxesRegiao = false;
        });

        for (String regiao : regioesCadastradas) {
            CheckBox chk = new CheckBox(regiao);
            chk.selectedProperty().addListener((obs, estavaMarcado, marcadoAgora) -> {
                if (atualizandoCheckBoxesRegiao) return;
                atualizandoCheckBoxesRegiao = true;
                boolean todasMarcadas = !checkBoxesRegioes.isEmpty()
                        && checkBoxesRegioes.stream().allMatch(CheckBox::isSelected);
                chkTodasRegioes.setSelected(todasMarcadas);
                atualizandoCheckBoxesRegiao = false;
            });
            checkBoxesRegioes.add(chk);
            vboxFiltroRegioes.getChildren().add(chk);
        }

        vboxFiltroRegioes.getChildren().add(chkTodasRegioes);
    }

    private void carregarTabela(List<Pesquisa> lista) {
        tblPesquisas.setItems(FXCollections.observableArrayList(lista));
    }

    private List<String> obterTiposMarcados() {
        List<String> lista = new ArrayList<>();
        if (chkTipoIntencao.isSelected()) lista.add("Intenção de voto");
        if (chkTipoConhecimento.isSelected()) lista.add("Conhecimento");
        if (chkTipoRejeicao.isSelected()) lista.add("Rejeição");
        if (chkTipoSegundoTurno.isSelected()) lista.add("Segundo turno simulado");
        return lista;
    }

    private List<String> obterRegioesMarcadas() {
        return checkBoxesRegioes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private List<Cargo> obterCargosMarcados() {
        List<Cargo> lista = new ArrayList<>();
        if (chkCargoPresidente.isSelected()) lista.add(Cargo.PRESIDENTE);
        if (chkCargoGovernador.isSelected()) lista.add(Cargo.GOVERNADOR);
        if (chkCargoSenador.isSelected()) lista.add(Cargo.SENADOR);
        if (chkCargoDepFederal.isSelected()) lista.add(Cargo.DEPUTADO_FEDERAL);
        if (chkCargoDepEstadual.isSelected()) lista.add(Cargo.DEPUTADO_ESTADUAL);
        return lista;
    }

    private List<String> obterSituacoesMarcadas() {
        List<String> lista = new ArrayList<>();
        if (chkSituacaoPlanejada.isSelected()) lista.add("Planejada");
        if (chkSituacaoEmColeta.isSelected()) lista.add("Em coleta");
        if (chkSituacaoEncerrada.isSelected()) lista.add("Encerrada");
        if (chkSituacaoProcessada.isSelected()) lista.add("Processada");
        return lista;
    }

    @FXML
    public void filtrarPesquisas(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String candidato = txtNomeCandidato.getText();
        LocalDate inicio = dpDataInicio.getValue();
        LocalDate fim = dpDataFim.getValue();

        List<Pesquisa> filtradas = getPesquisaController().buscarComFiltrosMultiplos(
                candidato, obterCargosMarcados(), obterTiposMarcados(),
                obterSituacoesMarcadas(), obterRegioesMarcadas(),
                null, inicio, fim, false, new ArrayList<>()
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

        chkTipoIntencao.setSelected(false);
        chkTipoConhecimento.setSelected(false);
        chkTipoRejeicao.setSelected(false);
        chkTipoSegundoTurno.setSelected(false);

        atualizandoCheckBoxesRegiao = true;
        if (chkTodasRegioes != null) chkTodasRegioes.setSelected(false);
        for (CheckBox chk : checkBoxesRegioes) {
            chk.setSelected(false);
        }
        atualizandoCheckBoxesRegiao = false;

        chkCargoPresidente.setSelected(false);
        chkCargoGovernador.setSelected(false);
        chkCargoSenador.setSelected(false);
        chkCargoDepFederal.setSelected(false);
        chkCargoDepEstadual.setSelected(false);

        chkSituacaoPlanejada.setSelected(false);
        chkSituacaoEmColeta.setSelected(false);
        chkSituacaoEncerrada.setSelected(false);
        chkSituacaoProcessada.setSelected(false);

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
        controller.setPesquisaEdicao(null);

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
        controller.setPesquisaEdicao(selecionada);

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