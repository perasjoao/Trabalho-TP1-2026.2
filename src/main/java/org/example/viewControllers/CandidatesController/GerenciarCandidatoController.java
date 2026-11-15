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

//tela de gerenciamento: escolhe uma candidatura para editar, mudar a situacao ou excluir
public class GerenciarCandidatoController extends TelaCandidatos {

    @FXML private ComboBox<Candidatura> caixaCandidatura;
    @FXML private TextField campoNome;
    @FXML private TextField campoNumero;
    @FXML private ComboBox<Cargo> caixaCargo;
    @FXML private ComboBox<SituacaoCandidatura> caixaSituacao;

    private final CandidatoController candidatoController = new CandidatoController();

    @FXML
    public void initialize() {
        caixaCargo.getItems().setAll(Cargo.values());
        caixaSituacao.getItems().setAll(SituacaoCandidatura.values());
        carregarCandidaturas();
    }

    private void carregarCandidaturas() {
        caixaCandidatura.getItems().setAll(candidatoController.listar());
    }

    //chamado quando o usuario escolhe uma candidatura na caixa
    @FXML
    public void selecionarCandidatura() {
        Candidatura c = caixaCandidatura.getValue();
        if (c == null) {
            limparCampos();
            return;
        }
        campoNome.setText(c.getCandidato().getNome());
        campoNumero.setText(String.valueOf(c.getNumero()));
        caixaCargo.setValue(c.getCargo());
        caixaSituacao.setValue(c.getSituacao());
    }

    @FXML
    public void atualizarCandidato() {
        Candidatura c = caixaCandidatura.getValue();
        if (c == null) {
            mostrarAviso("Selecione uma candidatura.");
            return;
        }
        //confere a situacao antes, para nao gravar a edicao e falhar depois na situacao
        if (caixaSituacao.getValue() == null) {
            mostrarAviso("A situação da candidatura é obrigatória.");
            return;
        }
        try {
            int numero = lerNumero(campoNumero.getText());
            candidatoController.editar(c, campoNome.getText(), numero, caixaCargo.getValue());
            candidatoController.alterarSituacao(c, caixaSituacao.getValue());
            mostrarSucesso("Candidatura atualizada: " + c.getDescricao());
            carregarCandidaturas();       //recarrega para a caixa mostrar o texto novo
            caixaCandidatura.setValue(c); //mantem a mesma candidatura selecionada
        } catch (DadoInvalidoException | CandidaturaDuplicadaException e) {
            mostrarAviso(e.getMessage());
        }
    }

    @FXML
    public void excluirCandidato() {
        Candidatura c = caixaCandidatura.getValue();
        if (c == null) {
            mostrarAviso("Selecione uma candidatura.");
            return;
        }
        if (!confirmar("Excluir a candidatura " + c.getDescricao() + "?\nEsta ação não pode ser desfeita.")) {
            return; //o usuario desistiu
        }
        candidatoController.excluir(c);
        mostrarSucesso("Candidatura excluída.");
        carregarCandidaturas();
        caixaCandidatura.setValue(null); //tambem limpa os campos, pelo selecionarCandidatura
    }

    private void limparCampos() {
        campoNome.clear();
        campoNumero.clear();
        caixaCargo.setValue(null);
        caixaSituacao.setValue(null);
    }
}