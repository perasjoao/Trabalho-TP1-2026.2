package org.example.viewControllers.CandidatesController;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.example.exceptions.DadoInvalidoException;
import org.example.models.candidatos.Candidatura;
import org.example.models.candidatos.Partido;
import org.example.serviceControllers.candidatos.CandidatoController;
import org.example.serviceControllers.candidatos.PartidoController;

//tela de associacao: escolhe uma candidatura e um partido ja cadastrados
public class AssociarCandidatoController extends TelaCandidatos {

    @FXML private ComboBox<Candidatura> caixaCandidatura;
    @FXML private ComboBox<Partido> caixaPartido;

    private final CandidatoController candidatoController = new CandidatoController();
    private final PartidoController partidoController = new PartidoController();

    @FXML
    public void initialize() {
        carregarCaixas();
    }

    //preenche as caixas com o que esta cadastrado no repositorio
    private void carregarCaixas() {
        caixaCandidatura.getItems().setAll(candidatoController.listar());
        caixaPartido.getItems().setAll(partidoController.listar());
    }

    @FXML
    public void associar() {
        Candidatura candidatura = caixaCandidatura.getValue();
        Partido partido = caixaPartido.getValue();

        //o controller nao confere a candidatura, entao a tela confere antes
        if (candidatura == null) {
            mostrarAviso("Selecione uma candidatura para a associação.");
            return;
        }
        if (candidatura.getPartido() == partido && partido != null) {
            mostrarAviso("Esta candidatura já pertence ao partido " + partido.getSigla() + ".");
            return;
        }

        try {
            candidatoController.associarAPartido(candidatura, partido);
            mostrarSucesso("Associação realizada: " + candidatura.getDescricao());
            carregarCaixas(); //recarrega para a caixa mostrar a sigla nova
            caixaCandidatura.setValue(null);
            caixaPartido.setValue(null);
        } catch (DadoInvalidoException e) {
            mostrarAviso(e.getMessage()); //partido nao selecionado
        }
    }
}