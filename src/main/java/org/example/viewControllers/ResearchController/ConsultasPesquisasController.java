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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.models.Entrevista;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PlanoAmostral;
import org.example.serviceControllers.ColetaServiceController;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.PesquisaController;
import org.example.serviceControllers.candidatos.CandidatoController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultasPesquisasController {

    private static List<Entrevista> entrevistasDoSistema = new ArrayList<>();

    public static void setEntrevistasDoSistema(List<Entrevista> entrevistas) {
        entrevistasDoSistema = (entrevistas != null) ? entrevistas : new ArrayList<>();
        GerenciarPesquisasController.getPesquisaController().sincronizarEntrevistasRealizadas(entrevistasDoSistema);
    }

    public static List<Entrevista> getEntrevistasDoSistema() {
        return entrevistasDoSistema;
    }

    public static void recarregarEntrevistasDoSistema() {
        List<Entrevista> carregadas = ColetaServiceController.getInstancia().listarEntrevistas();
        entrevistasDoSistema = (carregadas != null) ? carregadas : new ArrayList<>();
        GerenciarPesquisasController.getPesquisaController().sincronizarEntrevistasRealizadas(entrevistasDoSistema);
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
    @FXML private TextField txtQtdEntrevistados;
    @FXML private CheckBox chkAbaixoMeta;

    @FXML private Label lblAlertaMetas;
    @FXML private VBox vboxDetalheMeta;
    @FXML private Label lblDetalheMetaTitulo;
    @FXML private Label lblDetalheMetaConteudo;

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
    @FXML private TableColumn<Pesquisa, String> colMetaAmostral;

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

        colMetaAmostral.setCellValueFactory(cellData -> {
            Pesquisa p = cellData.getValue();
            PlanoAmostral plano = p.getPlanoAmostral();
            boolean semMetas = (plano == null)
                    || (plano.getMetasPorCategoria().isEmpty() && plano.getMetasPorGrupo().isEmpty());
            if (semMetas) {
                return new SimpleStringProperty("Sem metas");
            }
            boolean abaixo = GerenciarPesquisasController.getPesquisaController()
                    .estaAbaixoDaMetaAmostral(p, entrevistasDoSistema);
            return new SimpleStringProperty(abaixo ? "Abaixo da meta" : "Atingida");
        });

        colMetaAmostral.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Abaixo da meta".equals(item)) {
                        setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    } else if ("Atingida".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #7f8c8d;");
                    }
                }
            }
        });

        tblPesquisas.getSelectionModel().selectedItemProperty().addListener((obs, antiga, selecionada) ->
                atualizarPainelDetalheMeta(selecionada)
        );

        recarregarEntrevistasDoSistema();
        GerenciarPesquisasController.getPesquisaController().vincularCandidaturasCarregadas(new CandidatoController().listar());
        carregarTabela(GerenciarPesquisasController.getPesquisaController().getPesquisas());
    }

    private void atualizarPainelDetalheMeta(Pesquisa p) {
        if (p == null) {
            lblDetalheMetaTitulo.setText("Detalhamento das Metas do Plano Amostral");
            lblDetalheMetaConteudo.setText("Selecione uma pesquisa na tabela acima para visualizar o progresso de cada grupo da amostra.");
            lblDetalheMetaConteudo.setStyle("-fx-text-fill: #495057;");
            vboxDetalheMeta.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
            return;
        }

        PesquisaController controller = GerenciarPesquisasController.getPesquisaController();
        List<String> statusGrupos = controller.obterStatusGruposPlanoAmostral(p, entrevistasDoSistema);
        lblDetalheMetaTitulo.setText("Plano Amostral — " + p.getCodigo() + " (" + p.getNome() + "):");

        if (statusGrupos.isEmpty()) {
            lblDetalheMetaConteudo.setText("Esta pesquisa não possui metas mínimas por grupo configuradas no Plano Amostral.");
            lblDetalheMetaConteudo.setStyle("-fx-text-fill: #6c757d;");
            vboxDetalheMeta.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
            return;
        }

        boolean abaixo = controller.estaAbaixoDaMetaAmostral(p, entrevistasDoSistema);
        lblDetalheMetaConteudo.setText(String.join("   |   ", statusGrupos));
        if (abaixo) {
            lblDetalheMetaConteudo.setStyle("-fx-text-fill: #a93226; -fx-font-weight: bold;");
            vboxDetalheMeta.setStyle("-fx-background-color: #fdedec; -fx-border-color: #f5b7b1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        } else {
            lblDetalheMetaConteudo.setStyle("-fx-text-fill: #1e8449; -fx-font-weight: bold;");
            vboxDetalheMeta.setStyle("-fx-background-color: #eafaf1; -fx-border-color: #a9dfbf; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12 8 12;");
        }
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

        PesquisaController controller = GerenciarPesquisasController.getPesquisaController();
        long qtdAbaixoMeta = lista.stream()
                .filter(p -> controller.estaAbaixoDaMetaAmostral(p, entrevistasDoSistema))
                .count();

        if (qtdAbaixoMeta > 0) {
            lblAlertaMetas.setText(qtdAbaixoMeta + " pesquisa(s) abaixo da meta amostral");
            lblAlertaMetas.setStyle("-fx-background-color: #fdedec; -fx-text-fill: #c0392b; -fx-padding: 4 10 4 10; -fx-background-radius: 6; -fx-font-weight: bold;");
        } else {
            lblAlertaMetas.setText("Nenhuma pesquisa abaixo da meta");
            lblAlertaMetas.setStyle("-fx-background-color: #eafaf1; -fx-text-fill: #27ae60; -fx-padding: 4 10 4 10; -fx-background-radius: 6; -fx-font-weight: bold;");
        }

        // seleciona primeira pesquisa abaixo da meta
        Pesquisa primeiraAbaixo = lista.stream()
                .filter(p -> controller.estaAbaixoDaMetaAmostral(p, entrevistasDoSistema))
                .findFirst()
                .orElse(null);
        if (primeiraAbaixo != null) {
            tblPesquisas.getSelectionModel().select(primeiraAbaixo);
        } else {
            atualizarPainelDetalheMeta(tblPesquisas.getSelectionModel().getSelectedItem());
        }
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
        recarregarEntrevistasDoSistema();

        String codigo = txtCodigo.getText();
        String candidato = txtNomeCandidato.getText();
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

        List<Pesquisa> filtradas = GerenciarPesquisasController.getPesquisaController().buscarComFiltrosMultiplos(
                candidato, obterCargosMarcados(), obterTiposMarcados(),
                obterSituacoesMarcadas(), obterRegioesMarcadas(),
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
        txtQtdEntrevistados.clear();
        chkAbaixoMeta.setSelected(false);

        recarregarEntrevistasDoSistema();
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