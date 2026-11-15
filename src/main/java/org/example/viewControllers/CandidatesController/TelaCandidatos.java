package org.example.viewControllers.CandidatesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.exceptions.DadoInvalidoException;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import java.io.IOException;

//base das telas do modulo: navegacao, mensagens e leitura de campos
//cada tela herda daqui e so cuida dos seus proprios campos
public abstract class TelaCandidatos {

    //botao Voltar de todas as telas
    @FXML
    public void AbrirMenuCandidato(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Candidates/Candidatos.fxml");
    }

    //troca o conteudo da janela mantendo o tamanho atual
    protected void trocarTela(ActionEvent event, String caminhoFxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double largura = tela.getWidth();
        double altura = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(largura);
        tela.setHeight(altura);
    }

    protected void mostrarAviso(String mensagem) {
        mostrarAlerta(Alert.AlertType.WARNING, "Atenção", mensagem);
    }

    protected void mostrarSucesso(String mensagem) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", mensagem);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    //converte o texto digitado em numero
    //letras ou campo vazio viram mensagem clara
    protected int lerNumero(String texto) throws DadoInvalidoException {
        if (texto == null || texto.isBlank()) {
            throw new DadoInvalidoException("O número é obrigatório.");
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("O número deve conter apenas dígitos.");
        }
    }

    //pergunta antes de uma acao definitiva
    //devolve true so se o usuario clicar em OK
    protected boolean confirmar(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Optional<ButtonType> resposta = alerta.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.OK;
    }
}