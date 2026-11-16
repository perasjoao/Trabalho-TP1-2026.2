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
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoPartido;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de busca: filtra os partidos e mostra o resultado numa tabela
public class BuscarPartidoController extends TelaCandidatos {

    @FXML private TextField campoNome;
    @FXML private TextField campoSigla;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<SituacaoPartido> caixaSituacao;

    @FXML private TableView<Partido> tabelaPartidos;
    @FXML private TableColumn<Partido, String> colunaNome;
    @FXML private TableColumn<Partido, String> colunaSigla;
    @FXML private TableColumn<Partido, Integer> colunaNumero;
    @FXML private TableColumn<Partido, String> colunaSituacao;

    private final PartidoController partidoController = new PartidoController();

    @FXML
    public void initialize() {
        caixaSituacao.getItems().setAll(SituacaoPartido.values());

        //cada coluna recebe o partido da linha e devolve o que deve aparecer nela
        colunaNome.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getNome()));
        colunaSigla.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getSigla()));
        colunaNumero.setCellValueFactory(linha -> new SimpleObjectProperty<>(linha.getValue().getNumero()));
        colunaSituacao.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().getSituacao().toString()));

        tabelaPartidos.setPlaceholder(new Label("Nenhum partido encontrado."));
        tabelaPartidos.getItems().setAll(partidoController.listar()); //ao abrir mostra todos
    }

    @FXML
    public void pesquisarPartido() {
        try {
            //numero vazio = filtro ignorado
            //preenchido precisa ser valido
            Integer numero = campoNumero.getText().isBlank() ? null : lerNumero(campoNumero.getText());
            tabelaPartidos.getItems().setAll(partidoController.consultar(
                    campoNome.getText(), campoSigla.getText(), numero, caixaSituacao.getValue()));
        } catch (DadoInvalidoException e) {
            mostrarAviso(e.getMessage());
        }
    }

    @FXML
    public void limparFiltros() {
        campoNome.clear();
        campoSigla.clear();
        campoNumero.clear();
        caixaSituacao.setValue(null);
        tabelaPartidos.getItems().setAll(partidoController.listar());
    }
}