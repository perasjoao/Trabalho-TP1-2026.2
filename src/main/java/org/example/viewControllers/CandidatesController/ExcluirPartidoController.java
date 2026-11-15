package org.example.viewControllers.CandidatesController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.exceptions.PartidoComCandidaturasException;
import org.example.models.candidatos.Partido;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de exclusao: escolhe um partido, mostra seus dados e exclui apos confirmacao
public class ExcluirPartidoController extends TelaCandidatos {

    @FXML private ComboBox<Partido> caixaPartido;
    @FXML private TextField campoNome;
    @FXML private TextField campoSigla;
    @FXML private TextField campoNumero;

    private final PartidoController partidoController = new PartidoController();

    @FXML
    public void initialize() {
        carregarPartidos();
    }

    private void carregarPartidos() {
        caixaPartido.getItems().setAll(partidoController.listar());
    }

    //mostra os dados do partido escolhido
    @FXML
    public void selecionarPartido() {
        Partido p = caixaPartido.getValue();
        campoNome.setText(p == null ? "" : p.getNome());
        campoSigla.setText(p == null ? "" : p.getSigla());
        campoNumero.setText(p == null ? "" : String.valueOf(p.getNumero()));
    }

    @FXML
    public void excluirPartido() {
        Partido p = caixaPartido.getValue();
        if (p == null) {
            mostrarAviso("Selecione um partido.");
            return;
        }
        if (!confirmar("Excluir o partido " + p.getDescricao() + "?\nEsta ação não pode ser desfeita.")) {
            return;
        }
        try {
            partidoController.excluir(p);
            mostrarSucesso("Partido excluído.");
            carregarPartidos();
            caixaPartido.setValue(null);
        } catch (PartidoComCandidaturasException e) {
            mostrarAviso(e.getMessage()); //partido com candidaturas
        }
    }
}