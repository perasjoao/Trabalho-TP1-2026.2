package org.example.viewControllers.AnaliseController;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exceptions.CaixaPesquisaVazia;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.exceptions.RelatorioException;
import org.example.exceptions.RelatorioVazioException;
import org.example.models.Entrevista;
import org.example.models.Estatisticas;
import org.example.models.Relatorio;
import org.example.models.coleta.Resposta;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.ColetaServiceController;
import org.example.serviceControllers.PesquisaController;
import org.example.serviceControllers.RelatorioController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

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
    @FXML private TableColumn<LinhaTabelaRelatorio, String> colunaEst;

    @Override
    public void initialize(URL location, ResourceBundle resources) { //executa assim que a pagina carrega
        if (caixaPesquisa != null) { //para n dar erro se n carregar a caixa de selecao
            configurarTabela();
            carregarPesquisas();
            carregarParametros();
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
        colunaEst.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().percentual));
    }

    private void carregarParametros() {
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
        Set<String> opcoesEncontradas = new LinkedHashSet<>(); //para n ter duplicata
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

    private String limparFiltro(String valor) {
        if (valor == null || valor.trim().isEmpty() || valor.equalsIgnoreCase("Todos")) {
            return null;
        }
        return valor.trim();
    }

    private void preencherTabela(Relatorio relatorio) {
        ObservableList<LinhaTabelaRelatorio> linhas = FXCollections.observableArrayList();

        if (relatorio.getEstatisticas() != null) {
            for (Estatisticas est : relatorio.getEstatisticas()) {
                String grupoEPergunta = est.getParametro(); //

                if (est.getEstatistica() != null) {
                    for (String chave : est.getEstatistica().keySet()) {
                        Float pct = est.getEstatistica().get(chave);
                        // separa a string em nome e quantidade para as colunas da tabela
                        String nomeOpcao = chave;
                        String qtdTexto = "-";
                        if (chave.contains(" (")) {
                            String[] partes = chave.split(" \\(");
                            nomeOpcao = partes[0];
                            qtdTexto = partes[1].replace(")", "");
                        }
                        linhas.add(new LinhaTabelaRelatorio(
                                grupoEPergunta + " -> " + nomeOpcao,
                                qtdTexto,
                                pct + "%"
                        ));
                    }
                }
            }
        }
        idTabelaRelatorio.setItems(linhas);
    }

    private void trataAlerta(Alert.AlertType tipo, String titulo, String mensagem) { //cria um alerta na tela
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }


    @FXML
    public void acaoGerarRelatorio(ActionEvent event) {
        try {
            Pesquisa pesquisa = caixaPesquisa.getValue();
            if (pesquisa == null) {
                throw new CaixaPesquisaVazia("Selecione uma pesquisa antes de gerar o relatório.");
            }
            //pega as entrevistas da pesquisa
            List<Entrevista> entrevistasDaPesquisa = new ArrayList<>();
            for (Entrevista e : coletaController.listarEntrevistas()) {
                if (e.getCodigoPesquisa() != null && e.getCodigoPesquisa().equalsIgnoreCase(pesquisa.getCodigo())) {
                    entrevistasDaPesquisa.add(e);
                }
            }
            //pega os filtros selecionados
            String regiao = limparFiltro(caixaRegiao.getValue());
            String candidato = limparFiltro(caixaCandidato.getValue());
            LocalDate dataInicio = idDataInicio.getValue();
            LocalDate dataFim = idDataFinal.getValue();
            String filtroGenero = limparFiltro(caixaGenero.getValue());
            String filtroEscolaridade = limparFiltro(caixaEscolaridade.getValue());
            String filtroFaixaEtaria = limparFiltro(caixaFaixaEtaria.getValue());
            String filtroRenda = limparFiltro(caixaFaixaRenda.getValue());
            //filtro pelas as caracteristicas dos participantes
            List<Entrevista> entrevistasFiltradasAmostra = new ArrayList<>();
            for (Entrevista e : entrevistasDaPesquisa) {
                if (e.getParticipante() != null) {
                    if (filtroGenero != null && !filtroGenero.equalsIgnoreCase(e.getParticipante().getGenero())) {
                        continue;
                    }
                    if (filtroEscolaridade != null && !filtroEscolaridade.equalsIgnoreCase(e.getParticipante().getEscolaridade())) {
                        continue;
                    }
                    if (filtroFaixaEtaria != null && !filtroFaixaEtaria.equalsIgnoreCase(e.getParticipante().getFaixaEtaria())) {
                        continue;
                    }
                    if (regiao != null && !regiao.equalsIgnoreCase(e.getParticipante().getRegiao())) {
                        continue;
                    }
                    if (filtroRenda != null && !filtroRenda.equalsIgnoreCase(e.getParticipante().getFaixaRenda())) {
                        continue;
                    }
                }
                entrevistasFiltradasAmostra.add(e);
            }
            String titulo = "Relatório - " + pesquisa.getNome();
            this.relatorioAtual = relatorioController.gerarRelatorio(
                    pesquisa, titulo, entrevistasFiltradasAmostra, regiao, dataInicio, dataFim, candidato
            );
            preencherTabela(this.relatorioAtual);
        } catch (CaixaPesquisaVazia| PesquisaInvalidaException | RelatorioException e) {
            trataAlerta(Alert.AlertType.WARNING, "Aviso", e.getMessage()); //cria um alerta na tela
            idTabelaRelatorio.getItems().clear();
        } catch (IOException e) {
            trataAlerta(Alert.AlertType.ERROR, "Erro de Arquivo", "Erro ao processar arquivo: " + e.getMessage());
        }
    }

    @FXML
    public void acaoExportarCsv(ActionEvent event) {
        try {
            if (this.relatorioAtual == null) {
                throw new RelatorioVazioException("Gere um relatório primeiro antes de exportar para CSV.");
            }
            String nomeArquivo = "relatorio_" + relatorioAtual.getPesquisa().getCodigo() + ".csv";
            relatorioController.exportaRelatorio(this.relatorioAtual, nomeArquivo);
            trataAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Relatório exportado para dados/relatorios/" + nomeArquivo);
        } catch (RelatorioVazioException e) {
            trataAlerta(Alert.AlertType.WARNING, "Aviso", e.getMessage());
        } catch (IOException e) {
            trataAlerta(Alert.AlertType.ERROR, "Erro ao Exportar", "Não foi possível salvar o CSV: " + e.getMessage());
        }
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