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
import org.example.serviceControllers.ColetaServiceController;
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

    @FXML private CheckBox chkRegiaoNorte;
    @FXML private CheckBox chkRegiaoNordeste;
    @FXML private CheckBox chkRegiaoCentroOeste;
    @FXML private CheckBox chkRegiaoSudeste;
    @FXML private CheckBox chkRegiaoSul;
    @FXML private CheckBox chkRegiaoNacional;

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
            if ("Nacional".equalsIgnoreCase(p.getAbrangencia())) {
                return new SimpleStringProperty("Nacional");
            }
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

        recarregarEntrevistasDoSistema();
        GerenciarPesquisasController.getPesquisaController().vincularCandidaturasCarregadas(new CandidatoController().listar());
        carregarTabela(GerenciarPesquisasController.getPesquisaController().getPesquisas());
    }

    private void configurarCheckBoxesRegiao() {
        CheckBox[] regionais = {
                chkRegiaoNorte, chkRegiaoNordeste, chkRegiaoCentroOeste, chkRegiaoSudeste, chkRegiaoSul
        };

        chkRegiaoNacional.selectedProperty().addListener((obs, estavaMarcado, marcadoAgora) -> {
            if (atualizandoCheckBoxesRegiao) return;
            atualizandoCheckBoxesRegiao = true;
            for (CheckBox chk : regionais) {
                chk.setSelected(marcadoAgora);
            }
            atualizandoCheckBoxesRegiao = false;
        });

        for (CheckBox chk : regionais) {
            chk.selectedProperty().addListener((obs, estavaMarcado, marcadoAgora) -> {
                if (atualizandoCheckBoxesRegiao) return;
                atualizandoCheckBoxesRegiao = true;
                boolean todasMarcadas = chkRegiaoNorte.isSelected()
                        && chkRegiaoNordeste.isSelected()
                        && chkRegiaoCentroOeste.isSelected()
                        && chkRegiaoSudeste.isSelected()
                        && chkRegiaoSul.isSelected();
                chkRegiaoNacional.setSelected(todasMarcadas);
                atualizandoCheckBoxesRegiao = false;
            });
        }
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

    private List<Regiao> obterRegioesMarcadas() {
        List<Regiao> lista = new ArrayList<>();
        if (chkRegiaoNorte.isSelected()) lista.add(Regiao.NORTE);
        if (chkRegiaoNordeste.isSelected()) lista.add(Regiao.NORDESTE);
        if (chkRegiaoCentroOeste.isSelected()) lista.add(Regiao.CENTRO_OESTE);
        if (chkRegiaoSudeste.isSelected()) lista.add(Regiao.SUDESTE);
        if (chkRegiaoSul.isSelected()) lista.add(Regiao.SUL);
        if (chkRegiaoNacional.isSelected()) lista.add(Regiao.NACIONAL);
        return lista;
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

        chkRegiaoNorte.setSelected(false);
        chkRegiaoNordeste.setSelected(false);
        chkRegiaoCentroOeste.setSelected(false);
        chkRegiaoSudeste.setSelected(false);
        chkRegiaoSul.setSelected(false);
        chkRegiaoNacional.setSelected(false);

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