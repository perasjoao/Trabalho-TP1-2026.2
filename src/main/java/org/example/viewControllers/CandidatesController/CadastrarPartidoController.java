package org.example.viewControllers.CandidatesController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.exceptions.DadoInvalidoException;
import org.example.exceptions.PartidoDuplicadoException;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoPartido;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de cadastro de partido: le os campos e chama o PartidoController
public class CadastrarPartidoController extends TelaCandidatos {

    @FXML private TextField campoNome;
    @FXML private TextField campoSigla;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<SituacaoPartido> caixaSituacao;

    private final PartidoController partidoController = new PartidoController();

    //chamado automaticamente pelo JavaFX depois de ligar os campos do FXML
    @FXML
    public void initialize() {
        caixaSituacao.getItems().setAll(SituacaoPartido.values());
    }

    @FXML
    public void cadastrarPartido() {
        try {
            int numero = lerNumero(campoNumero.getText());
            Partido partido = partidoController.cadastrar(campoNome.getText(), campoSigla.getText(),
                    numero, caixaSituacao.getValue());
            mostrarSucesso("Partido cadastrado: " + partido.getDescricao());
            limparCampos();
        } catch (DadoInvalidoException | PartidoDuplicadoException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void limparCampos() {
        campoNome.clear();
        campoSigla.clear();
        campoNumero.clear();
        caixaSituacao.setValue(null);
    }
}