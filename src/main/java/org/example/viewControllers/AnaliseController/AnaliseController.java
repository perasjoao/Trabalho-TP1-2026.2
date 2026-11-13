package org.example.viewControllers.AnaliseController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.Entrevista;
import org.example.models.Relatorio;
import org.example.models.coleta.Resposta;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.ColetaServiceController;
import org.example.serviceControllers.PesquisaController;
import org.example.serviceControllers.RelatorioController;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class AnaliseController implements Initializable { //implenta a interface que executa o codigo assim q a pagina carrega
    private final RelatorioController relatorioController = new RelatorioController();
    private final PesquisaController pesquisaController = new PesquisaController();
    private final ColetaServiceController coletaController = new ColetaServiceController();
    private Relatorio relatorioAtual;

    @FXML private ComboBox<Pesquisa> caixaPesquisa;
    @FXML private ComboBox<String> caixaRegiao;
    @FXML private ComboBox<String> caixaCandidato;
    @FXML private DatePicker idDataInicio;
    @FXML private DatePicker idDataFinal;

    @FXML private ComboBox<String> caixaFaixaEtaria;
    @FXML private ComboBox<String> caixaEscolaridade;
    @FXML private ComboBox<String> caixaGenero;
    @FXML private ComboBox<String> caixaFaixaRenda;

    @FXML private TableView<LinhaTabelaRelatorio> idTabelaRelatorio;
    @FXML private TableColumn<LinhaTabelaRelatorio, String> colunaOpcao;
    @FXML private TableColumn<LinhaTabelaRelatorio, String> colunaQuantidade;
    @FXML private TableColumn<LinhaTabelaRelatorio, String> colunaPercentual;

    @Override
    public void initialize(URL location, ResourceBundle resources) { //executa assim que a pagina carrega
        if (caixaPesquisa != null) { //para n dar erro se n carregar a caixa de selecao
            //configurarTabela();
            //carregarPesquisas();
            //carregarFiltrosDemograficos();
        }
    }

    private void carregarPesquisas() {
        try {
            List<Pesquisa> lista = pesquisaController.listarTodas();
            caixaPesquisa.setItems(FXCollections.observableArrayList(lista));
        } catch (PesquisaInvalidaException e) {
            caixaPesquisa.setItems(FXCollections.observableArrayList());
        }
    }

    private void configurarTabela() {
        colunaOpcao.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().opcao));
        colunaQuantidade.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().quantidade));
        colunaPercentual.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().percentual));
    }

    private void carregarFiltrosDemograficos() {
        //chamar meotodo que lista todos os parametros, esses sao so para testes
        caixaGenero.setItems(FXCollections.observableArrayList("Todos", "Masculino", "Feminino", "Outro"));
        caixaEscolaridade.setItems(FXCollections.observableArrayList("Todos", "Fundamental", "Médio", "Superior"));
        caixaFaixaEtaria.setItems(FXCollections.observableArrayList("Todos", "16-24", "25-34", "35-44", "45-59", "60+"));
    }

    //funcao para facilitar a troca da tela atual para a do caminho informado
    private void trocarTela(ActionEvent event, String caminhoFxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    //botoes dentro do menu de analise
    @FXML
    public void abrirResultados(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/Resultados.fxml");
    }

    @FXML
    public void abrirProcessarPesquisa(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/ProcessarPesquisa.fxml");
    }

    @FXML
    public void abrirCompararPesquisas(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/CompararPesquisas.fxml");
    }

    @FXML
    public void abrirRelatorios(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/Relatorios.fxml");
    }

    //botao para voltar ao menu de analise
    @FXML
    public void abrirMenuAnalise(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Analise/MenuAnalise.fxml");
    }

    //botao para voltar ao menu principal
    @FXML
    public void voltarMenuPrincipal(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Menu/Menu.fxml");
    }

    @FXML
    public void selecionaPesquisa(ActionEvent event) {
        Pesquisa selecionada = caixaPesquisa.getValue();
        if (selecionada == null) return;
        //pega as informacoes da pesquisa selecionada
        Set<String> opcoesEncontradas = new LinkedHashSet<>();
        opcoesEncontradas.add("Todos");

        List<Entrevista> todas = coletaController.listarEntrevistas();
        for (Entrevista e : todas) {
            if (e.getCodigoPesquisa() != null && e.getCodigoPesquisa().equalsIgnoreCase(selecionada.getCodigo())) {
                if (e.getRespostas() != null) {
                    for (Resposta r : e.getRespostas()) {
                        if (r.getOpcaoEscolhida() != null) {
                            opcoesEncontradas.add(r.getOpcaoEscolhida());
                        }
                    }
                }
            }
        }
        caixaCandidato.setItems(FXCollections.observableArrayList(opcoesEncontradas));
    }

}
class LinhaTabelaRelatorio {
    final String opcao;
    final String quantidade;
     final String percentual;

    public LinhaTabelaRelatorio(String opcao, String quantidade, String percentual) {
        this.opcao = opcao;
        this.quantidade = quantidade;
        this.percentual = percentual;
    }
}