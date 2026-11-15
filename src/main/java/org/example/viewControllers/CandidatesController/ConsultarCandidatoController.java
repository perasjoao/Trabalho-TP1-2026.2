package org.example.viewControllers.CandidatesController;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.exceptions.DadoInvalidoException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoCandidatura;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.PesquisaController;
import org.example.serviceControllers.candidatos.CandidatoController;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de consulta: filtra as candidaturas por varios criterios, inclusive por pesquisa
public class ConsultarCandidatoController extends TelaCandidatos {

    @FXML private TextField campoNome;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<Cargo> caixaCargo;
    @FXML private ComboBox<Partido> caixaPartido;
    @FXML private ComboBox<Pesquisa> caixaPesquisa;
    @FXML private ComboBox<SituacaoCandidatura> caixaSituacao;

    @FXML private TableView<Candidatura> tabelaCandidaturas;
    @FXML private TableColumn<Candidatura, String> colunaNome;
    @FXML private TableColumn<Candidatura, Integer> colunaNumero;
    @FXML private TableColumn<Candidatura, String> colunaCargo;
    @FXML private TableColumn<Candidatura, String> colunaPartido;
    @FXML private TableColumn<Candidatura, String> colunaSituacao;

    private final CandidatoController candidatoController = new CandidatoController();

    @FXML
    public void initialize() {
        caixaCargo.getItems().setAll(Cargo.values());
        caixaSituacao.getItems().setAll(SituacaoCandidatura.values());
        caixaPartido.getItems().setAll(new PartidoController().listar());
        carregarPesquisas();

        colunaNome.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getCandidato().getNome()));
        colunaNumero.setCellValueFactory(linha -> new SimpleObjectProperty<>(linha.getValue().getNumero()));
        colunaCargo.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getCargo().toString()));
        colunaPartido.setCellValueFactory(linha -> new SimpleStringProperty(
                linha.getValue().getPartido() == null ? "—" : linha.getValue().getPartido().getSigla()));
        colunaSituacao.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getSituacao().toString()));

        tabelaCandidaturas.setPlaceholder(new Label("Nenhuma candidatura encontrada."));
        tabelaCandidaturas.getItems().setAll(candidatoController.listar()); //ao abrir, mostra todas (RF-CAN-1 listar)
    }

    //as pesquisas vem do modulo de Pesquisas
    private void carregarPesquisas() {
        try {
            caixaPesquisa.getItems().setAll(new PesquisaController().listarTodas());
        } catch (PesquisaInvalidaException e) {
            //sem pesquisas cadastradas: a caixa fica vazia e o filtro simplesmente nao e usado
        }
    }

    @FXML
    public void pesquisarCandidato() {
        try {
            Integer numero = campoNumero.getText().isBlank() ? null : lerNumero(campoNumero.getText());
            tabelaCandidaturas.getItems().setAll(candidatoController.consultar(campoNome.getText(), numero,
                    caixaCargo.getValue(), caixaPartido.getValue(), caixaSituacao.getValue(), caixaPesquisa.getValue()));
        } catch (DadoInvalidoException e) {
            mostrarAviso(e.getMessage());
        }
    }

    @FXML
    public void limparFiltros() {
        campoNome.clear();
        campoNumero.clear();
        caixaCargo.setValue(null);
        caixaPartido.setValue(null);
        caixaPesquisa.setValue(null);
        caixaSituacao.setValue(null);
        tabelaCandidaturas.getItems().setAll(candidatoController.listar());
    }
}