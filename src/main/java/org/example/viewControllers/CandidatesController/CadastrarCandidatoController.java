package org.example.viewControllers.CandidatesController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.exceptions.CandidaturaDuplicadaException;
import org.example.exceptions.DadoInvalidoException;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.SituacaoCandidatura;
import org.example.models.comum.Cargo;
import org.example.serviceControllers.candidatos.CandidatoController;

//tela de cadastro de candidato: le os campos e chama o CandidatoController
public class CadastrarCandidatoController extends TelaCandidatos {

    @FXML private TextField campoNome;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<Cargo> caixaCargo;
    @FXML private ComboBox<SituacaoCandidatura> caixaSituacao;

    private final CandidatoController candidatoController = new CandidatoController();

    //chamado automaticamente pelo JavaFX depois de ligar os campos do FXML
    @FXML
    public void initialize() {
        caixaCargo.getItems().setAll(Cargo.values());
        caixaSituacao.getItems().setAll(SituacaoCandidatura.values());
    }

    @FXML
    public void cadastrarCandidato() {
        try {
            int numero = lerNumero(campoNumero.getText());
            Candidatura candidatura = candidatoController.cadastrar(campoNome.getText(), numero,
                    caixaCargo.getValue(), caixaSituacao.getValue());
            mostrarSucesso("Candidatura cadastrada: " + candidatura.getDescricao());
            limparCampos();
        } catch (DadoInvalidoException | CandidaturaDuplicadaException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void limparCampos() {
        campoNome.clear();
        campoNumero.clear();
        caixaCargo.setValue(null);
        caixaSituacao.setValue(null);
    }
}