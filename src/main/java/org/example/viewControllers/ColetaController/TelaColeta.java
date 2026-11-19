package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.serviceControllers.ColetaServiceController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//base das telas da coleta: navegacao, mensagens e leitura de campos
//cada tela herda daqui e so cuida dos proprios campos
public abstract class TelaColeta {

    protected static final String MENU_COLETA = "/view.Coleta/MenuColeta.fxml";
    protected static final String CONFIG_AMOSTRA = "/view.Coleta/ConfigAmostra.fxml";
    protected static final String REGISTRO_RESPOSTAS = "/view.Coleta/RegistrarRespostas.fxml";
    protected static final String CONSULTA_ENTREVISTAS = "/view.Coleta/FiltroEntrevistas.fxml";

    //genero nao e parametro do admin, entao fica aqui (mesma lista que a analise usa)
    protected static final List<String> GENEROS = List.of("Masculino", "Feminino", "Outro");

    protected final ColetaServiceController coleta = ColetaServiceController.getInstancia();

    @FXML
    public void voltarParaMenu(ActionEvent event) throws IOException {
        trocarTela(event, MENU_COLETA);
    }

    protected void trocarTela(ActionEvent event, String caminhoFxml) throws IOException {
        Parent comeco = FXMLLoader.load(getClass().getResource(caminhoFxml));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double largura = tela.getWidth();
        double altura = tela.getHeight();
        tela.getScene().setRoot(comeco);
        tela.setWidth(largura);
        tela.setHeight(altura);
    }

    protected void mostrarAviso(String mensagem) {
        mostrarAlerta(Alert.AlertType.WARNING, "Atenção", mensagem);
    }

    protected void mostrarSucesso(String mensagem) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Tudo certo", mensagem);
    }

    protected boolean confirmar(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Optional<ButtonType> resposta = alerta.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.OK;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    protected String texto(TextField campo) {
        return (campo == null || campo.getText() == null) ? "" : campo.getText().trim();
    }

    //"Todas"/"Todos" no combo de filtro significa sem filtro
    protected String valorFiltro(ComboBox<String> combo) {
        String valor = combo.getValue();
        if (valor == null || valor.startsWith("Tod")) return null;
        return valor;
    }
}
