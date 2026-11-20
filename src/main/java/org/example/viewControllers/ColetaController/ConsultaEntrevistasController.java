package org.example.viewControllers.ColetaController;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.Entrevista;
import org.example.models.coleta.Participante;
import org.example.models.coleta.Resposta;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.PesquisaController;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;

//lista as entrevistas gravadas e filtra pelas caracteristicas da amostra
public class ConsultaEntrevistasController extends TelaColeta implements Initializable {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private ComboBox<String> cbFiltroPesquisa;
    @FXML private ComboBox<String> cbFiltroFaixaEtaria;
    @FXML private ComboBox<String> cbFiltroEscolaridade;
    @FXML private ComboBox<String> cbFiltroGenero;
    @FXML private ComboBox<String> cbFiltroRegiao;
    @FXML private ComboBox<String> cbFiltroFaixaRenda;
    @FXML private TextField txtFiltroEntrevistador;
    @FXML private DatePicker dpFiltroInicio;
    @FXML private DatePicker dpFiltroFim;

    @FXML private TableView<Entrevista> tabelaEntrevistas;
    @FXML private TableColumn<Entrevista, String> colEntrevista;
    @FXML private TableColumn<Entrevista, String> colPesquisa;
    @FXML private TableColumn<Entrevista, String> colData;
    @FXML private TableColumn<Entrevista, String> colEntrevistador;
    @FXML private TableColumn<Entrevista, String> colParticipante;
    @FXML private TableColumn<Entrevista, String> colRegiao;
    @FXML private TableColumn<Entrevista, String> colFaixaEtaria;
    @FXML private TableColumn<Entrevista, String> colGenero;
    @FXML private TableColumn<Entrevista, String> colEscolaridade;
    @FXML private TableColumn<Entrevista, String> colRenda;
    @FXML private TableColumn<Entrevista, String> colValidas;

    @FXML private Label lblTotal;
    @FXML private ListView<String> listaRespostas;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarFiltros();
        configurarColunas();
        tabelaEntrevistas.setPlaceholder(new Label("Nenhuma entrevista encontrada."));
        tabelaEntrevistas.getSelectionModel().selectedItemProperty()
                .addListener((obs, antes, selecionada) -> mostrarRespostas(selecionada));
        filtrar();
    }

    private void carregarFiltros() {
        ParametroController parametros = new ParametroController();
        preencher(cbFiltroFaixaEtaria, "Todas", parametros.listarFaixasEtarias());
        preencher(cbFiltroEscolaridade, "Todas", parametros.listarEscolaridades());
        preencher(cbFiltroGenero, "Todos", GENEROS);
        preencher(cbFiltroRegiao, "Todas", parametros.listarRegioes());
        preencher(cbFiltroFaixaRenda, "Todas", parametros.listarFaixasRenda());

        cbFiltroPesquisa.getItems().add("Todas");
        try {
            new PesquisaController().listarTodas()
                    .forEach(pesquisa -> cbFiltroPesquisa.getItems().add(pesquisa.getCodigo() + " - " + pesquisa.getNome()));
        } catch (PesquisaInvalidaException erro) {
            //sem pesquisas cadastradas, fica so o "Todas"
        }
        cbFiltroPesquisa.setValue("Todas");
    }

    private void preencher(ComboBox<String> combo, String todos, List<String> itens) {
        combo.getItems().add(todos);
        combo.getItems().addAll(itens);
        combo.setValue(todos);
    }

    private void configurarColunas() {
        coluna(colEntrevista, Entrevista::getIdEntrevista);
        coluna(colPesquisa, Entrevista::getCodigoPesquisa);
        coluna(colData, entrevistaFiltro -> entrevistaFiltro.getData() == null ? "" : entrevistaFiltro.getData().format(FORMATO_DATA));
        coluna(colEntrevistador, entrevistaFiltro -> entrevistaFiltro.getEntrevistador() == null ? "" : entrevistaFiltro.getEntrevistador().getNome());
        coluna(colParticipante, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getCodigo));
        coluna(colRegiao, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getRegiao));
        coluna(colFaixaEtaria, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getFaixaEtaria));
        coluna(colGenero, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getGenero));
        coluna(colEscolaridade, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getEscolaridade));
        coluna(colRenda, entrevistaFiltro -> participante(entrevistaFiltro, Participante::getFaixaRenda));
        coluna(colValidas, this::resumoValidas);
    }

    private void coluna(TableColumn<Entrevista, String> coluna, Function<Entrevista, String> valor) {
        coluna.setCellValueFactory(cel -> new ReadOnlyStringWrapper(valor.apply(cel.getValue())));
    }

    private String participante(Entrevista entrevistaFiltro, Function<Participante, String> campo) {
        if (entrevistaFiltro.getParticipante() == null) return "";
        String valor = campo.apply(entrevistaFiltro.getParticipante());
        return valor == null ? "" : valor;
    }

    //quantas respostas entram no percentual (sem contar "nao sabe" etc.)
    private String resumoValidas(Entrevista entrevistaFiltro) {
        List<Resposta> respostas = entrevistaFiltro.getRespostas();
        if (respostas == null || respostas.isEmpty()) return "0/0";
        long validas = respostas.stream().filter(Resposta::contaNoPercentual).count();
        return validas + "/" + respostas.size();
    }

    @FXML
    public void filtrar() {
        List<Entrevista> encontradas = coleta.filtrar(montarFiltro());
        tabelaEntrevistas.getItems().setAll(encontradas);
        lblTotal.setText(encontradas.size() + (encontradas.size() == 1 ? " entrevistaFiltro encontrada" : " entrevistas encontradas"));
        listaRespostas.getItems().clear();
    }

    @FXML
    public void limparFiltros(ActionEvent event) {
        for (ComboBox<String> combo : List.of(cbFiltroPesquisa, cbFiltroFaixaEtaria, cbFiltroEscolaridade,
                cbFiltroGenero, cbFiltroRegiao, cbFiltroFaixaRenda)) {
            combo.getSelectionModel().selectFirst();
        }
        txtFiltroEntrevistador.clear();
        dpFiltroInicio.setValue(null);
        dpFiltroFim.setValue(null);
        filtrar();
    }

    //junta so os filtros preenchidos; o que ficar em "Todas" nao filtra nada
    private Predicate<Entrevista> montarFiltro() {// comeca com um filtro que aceita tudo, e vai adicionando os filtros que o usuario preencheu em sequencia com o and, pra ir filtrando cada vez mais
        Predicate<Entrevista> filtro = entrevistaFiltro -> true;

        String pesquisa = valorFiltro(cbFiltroPesquisa);
        if (pesquisa != null) {
            String codigo = pesquisa.substring(0, pesquisa.indexOf(" - "));
            filtro = filtro.and(entrevistaFiltro -> codigo.equalsIgnoreCase(entrevistaFiltro.getCodigoPesquisa()));
        }
        filtro = filtro.and(igual(valorFiltro(cbFiltroFaixaEtaria), Participante::getFaixaEtaria));
        filtro = filtro.and(igual(valorFiltro(cbFiltroEscolaridade), Participante::getEscolaridade));
        filtro = filtro.and(igual(valorFiltro(cbFiltroGenero), Participante::getGenero));
        filtro = filtro.and(igual(valorFiltro(cbFiltroRegiao), Participante::getRegiao));
        filtro = filtro.and(igual(valorFiltro(cbFiltroFaixaRenda), Participante::getFaixaRenda));

        String entrevistador = texto(txtFiltroEntrevistador).toLowerCase();
        if (!entrevistador.isEmpty()) {
            filtro = filtro.and(entrevistaFiltro -> entrevistaFiltro.getEntrevistador() != null
                    && (contem(entrevistaFiltro.getEntrevistador().getCodigo(), entrevistador)
                    || contem(entrevistaFiltro.getEntrevistador().getNome(), entrevistador)));
        }

        LocalDate inicio = dpFiltroInicio.getValue();
        LocalDate fim = dpFiltroFim.getValue();
        if (inicio != null) filtro = filtro.and(entrevistaFiltro -> entrevistaFiltro.getData() != null && !entrevistaFiltro.getData().isBefore(inicio));
        if (fim != null) filtro = filtro.and(entrevistaFiltro -> entrevistaFiltro.getData() != null && !entrevistaFiltro.getData().isAfter(fim));
        return filtro;
    }

    private Predicate<Entrevista> igual(String esperado, Function<Participante, String> campo) {
        if (esperado == null) return entrevistaFiltro -> true;
        return entrevistaFiltro -> entrevistaFiltro.getParticipante() != null && esperado.equalsIgnoreCase(campo.apply(entrevistaFiltro.getParticipante()));
    }

    private boolean contem(String texto, String trecho) {
        return texto != null && texto.toLowerCase().contains(trecho);
    }

    private void mostrarRespostas(Entrevista entrevista) {
        listaRespostas.getItems().clear();
        if (entrevista == null || entrevista.getRespostas() == null) return;
        int i = 1;
        for (Resposta resp : entrevista.getRespostas()) {
            listaRespostas.getItems().add(i++ + ". " + resp.getTextoPergunta() + "  →  " + resp.getOpcaoEscolhida()
                    + (resp.contaNoPercentual() ? "" : "  (fora do percentual)"));
        }
    }
}
