package org.example.viewControllers.CandidatesController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.exceptions.DadoInvalidoException;
import org.example.exceptions.PartidoDuplicadoException;
import org.example.models.candidatos.Partido;
import org.example.models.candidatos.SituacaoPartido;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de atualizacao: escolhe um partido e altera nome, sigla, numero e situacao
public class AtualizarPartidoController extends TelaCandidatos {

    @FXML private ComboBox<Partido> caixaPartido;
    @FXML private TextField campoNome;
    @FXML private TextField campoSigla;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<SituacaoPartido> caixaSituacao;

    private final PartidoController partidoController = new PartidoController();

    @FXML
    public void initialize() {
        caixaSituacao.getItems().setAll(SituacaoPartido.values());
        carregarPartidos();
    }

    private void carregarPartidos() {
        caixaPartido.getItems().setAll(partidoController.listar());
    }

    //preenche os campos com os dados do partido escolhido
    @FXML
    public void selecionarPartido() {
        Partido p = caixaPartido.getValue();
        if (p == null) {
            limparCampos();
            return;
        }
        campoNome.setText(p.getNome());
        campoSigla.setText(p.getSigla());
        campoNumero.setText(String.valueOf(p.getNumero()));
        caixaSituacao.setValue(p.getSituacao());
    }

    @FXML
    public void atualizarPartido() {
        Partido p = caixaPartido.getValue();
        if (p == null) {
            mostrarAviso("Selecione um partido.");
            return;
        }
        try {
            int numero = lerNumero(campoNumero.getText());
            //o editar valida tudo antes de alterar, entao nao ha atualizacao pela metade
            partidoController.editar(p, campoNome.getText(), campoSigla.getText(), numero, caixaSituacao.getValue());
            mostrarSucesso("Partido atualizado: " + p.getDescricao());
            carregarPartidos();
            caixaPartido.setValue(p);
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