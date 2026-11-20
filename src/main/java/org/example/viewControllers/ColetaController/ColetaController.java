package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

//menu do modulo de coleta e amostra
public class ColetaController extends TelaColeta {

    @FXML
    public void abrirConfigAmostra(ActionEvent event) throws IOException {
        trocarTela(event, CONFIG_AMOSTRA);
    }

    //sem participante configurado nao tem como registrar, entao manda configurar primeiro
    @FXML
    public void abrirRegistroRespostas(ActionEvent event) throws IOException {//só permite fazer se ja tiver passado pela primeira tela que configura o entrevistador e o participante da entrevista
        if (!coleta.temAmostraConfigurada()) {
            mostrarAviso("Antes de registrar as respostas, configure a amostra (entrevistador e participante).");
            trocarTela(event, CONFIG_AMOSTRA);
            return;
        }
        trocarTela(event, REGISTRO_RESPOSTAS);
    }

    @FXML
    public void abrirConsultaEntrevistas(ActionEvent event) throws IOException {
        trocarTela(event, CONSULTA_ENTREVISTAS);
    }

    @FXML
    public void voltarMenuPrincipal(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Menu/Menu.fxml");
    }
}
