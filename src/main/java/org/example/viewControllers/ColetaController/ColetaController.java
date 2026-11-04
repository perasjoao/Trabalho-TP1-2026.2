package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import org.example.exceptions.AmostraInvalidaException;
import org.example.exceptions.ColetaIncompletaException;
import org.example.exceptions.EntrevistadorInvalidoException;
import org.example.models.Entrevista;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.models.coleta.Resposta;
import org.example.serviceControllers.ColetaServiceController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ColetaController implements Initializable {

    private static final ColetaServiceController coletaService = new ColetaServiceController();

    // Campos da tela ConfigAmostra.fxml
    @FXML private TextField txtCodigoEntrevistador;
    @FXML private TextField txtNomeEntrevistador;

    @FXML private ComboBox<String> cbFaixaEtaria;
    @FXML private ComboBox<String> cbEscolaridade;
    @FXML private ComboBox<String> cbGenero;
    @FXML private ComboBox<String> cbRegiao;
    @FXML private ComboBox<String> cbFaixaRenda;

    // Campos da tela RegistrarRespostas.fxml
    @FXML private Label lblP1;
    @FXML private ComboBox<String> cbP1;
    @FXML private ToggleGroup GROUP1;

    @FXML private Label lblP2;
    @FXML private ComboBox<String> cbP2;
    @FXML private ToggleGroup GROUP2;

    @FXML private Label lblP3;
    @FXML private ComboBox<String> cbP3;
    @FXML private ToggleGroup GROUP3;

    @FXML private Label lblP4;
    @FXML private ComboBox<String> cbP4;
    @FXML private ToggleGroup GROUP4;

    @FXML private ComboBox<String> cbPesquisaPertencente;

    // Campos da tela FiltroEntrevistas.fxml
    @FXML private ComboBox<String> cbFiltroEscolaridade;
    @FXML private ComboBox<String> cbFiltroGenero;
    @FXML private ComboBox<String> cbFiltroRegiao;
    @FXML private ComboBox<String> cbFiltroFaixaRenda;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Popula Amostra
        if (cbFaixaEtaria != null) cbFaixaEtaria.getItems().addAll("16-24 anos", "25-34 anos", "35-59 anos", "60+ anos");
        if (cbEscolaridade != null) cbEscolaridade.getItems().addAll("Ensino Fundamental", "Ensino Médio", "Ensino Superior", "Pós-Graduação");
        if (cbGenero != null) cbGenero.getItems().addAll("Masculino", "Feminino", "Outro", "Não informado");
        if (cbRegiao != null) cbRegiao.getItems().addAll("Norte", "Sul", "Leste", "Oeste", "Centro");
        if (cbFaixaRenda != null) cbFaixaRenda.getItems().addAll("Até 1 SM", "1 a 3 SM", "3 a 5 SM", "Mais de 5 SM");

        // Popula Filtro de Entrevistas
        if (cbFiltroEscolaridade != null) cbFiltroEscolaridade.getItems().addAll("Todas", "Ensino Fundamental", "Ensino Médio", "Ensino Superior", "Pós-Graduação");
        if (cbFiltroGenero != null) cbFiltroGenero.getItems().addAll("Todos", "Masculino", "Feminino", "Outro", "Não informado");
        if (cbFiltroRegiao != null) cbFiltroRegiao.getItems().addAll("Todas", "Norte", "Sul", "Leste", "Oeste", "Centro");
        if (cbFiltroFaixaRenda != null) cbFiltroFaixaRenda.getItems().addAll("Todas", "Até 1 SM", "1 a 3 SM", "3 a 5 SM", "Mais de 5 SM");

        // Popula Registro de Pesquisa
        if (cbP1 != null) cbP1.getItems().addAll("Candidato A", "Candidato B", "Candidato C");
        if (cbP2 != null) cbP2.getItems().addAll("Candidato A", "Candidato B", "Candidato C");
        if (cbP3 != null) cbP3.getItems().addAll("Sim", "Não", "Parcialmente");
        if (cbP4 != null) cbP4.getItems().addAll("Sim", "Não");
        if (cbPesquisaPertencente != null) cbPesquisaPertencente.getItems().addAll("Pesquisa Eleitoral 2026", "Pesquisa Local");
    }

    private void salvarAmostraAtual() {
        if (txtCodigoEntrevistador != null && txtNomeEntrevistador != null) {
            Entrevistador entrevistador = new Entrevistador(
                    txtCodigoEntrevistador.getText(),
                    txtNomeEntrevistador.getText()
            );

            Participante participante = new Participante(
                    "P" + (System.currentTimeMillis() % 1000),
                    cbFaixaEtaria != null ? cbFaixaEtaria.getValue() : "",
                    cbEscolaridade != null ? cbEscolaridade.getValue() : "",
                    cbGenero != null ? cbGenero.getValue() : "",
                    cbRegiao != null ? cbRegiao.getValue() : "",
                    cbFaixaRenda != null ? cbFaixaRenda.getValue() : ""
            );

            coletaService.salvarConfiguracaoAmostra(entrevistador, participante);
            System.out.println("Amostra guardada no ColetaServiceController!");
        }
    }

    private String obterOpcaoSelecionada(ComboBox<String> cb, ToggleGroup group) {
        if (group != null && group.getSelectedToggle() != null) {
            RadioButton rb = (RadioButton) group.getSelectedToggle();
            return rb.getText();
        }
        if (cb != null && cb.getValue() != null) {
            return cb.getValue();
        }
        return "Sem resposta";
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void salvarRegistro(ActionEvent event) throws IOException {
        salvarAmostraAtual();
        voltarParaMenu(event);
    }

    @FXML
    public void realizarPesquisa(ActionEvent event) throws IOException {
        voltarParaMenu(event);
    }

    @FXML
    public void ProxPesquisa(ActionEvent event) throws IOException {
        salvarAmostraAtual();

        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/RegistrarRespostas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void ConfigAm(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/ConfigAmostra.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void abrirFiltroEntrevistas(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/FiltroEntrevistas.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void voltarParaMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Coleta/MenuColeta.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void voltarMenuPrincipal(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Menu/Menu.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double width = tela.getWidth();
        double height = tela.getHeight();
        tela.getScene().setRoot(root);
        tela.setWidth(width);
        tela.setHeight(height);
    }

    @FXML
    public void finalizarEntrevista(ActionEvent event) throws IOException {
        try {
            Entrevistador entrevistador = coletaService.getEntrevistadorAtual();
            Participante participante = coletaService.getParticipanteAtual();

            // 1. Validação do Entrevistador
            if (entrevistador == null ||
                    entrevistador.getCodigo() == null || entrevistador.getCodigo().trim().isEmpty() ||
                    entrevistador.getNome() == null || entrevistador.getNome().trim().isEmpty()) {

                throw new EntrevistadorInvalidoException("Nenhum entrevistador válido associado! Por favor, configure a amostra antes de continuar.");
            }

            // 2. Validação da Amostra do Participante
            if (participante == null) {
                throw new AmostraInvalidaException("Configuração do participante da amostra está incompleta!");
            }

            // 3. Validação das Respostas
            List<Resposta> listaRespostas = new ArrayList<>();
            if (lblP1 != null) listaRespostas.add(new Resposta("P1", lblP1.getText(), obterOpcaoSelecionada(cbP1, GROUP1)));
            if (lblP2 != null) listaRespostas.add(new Resposta("P2", lblP2.getText(), obterOpcaoSelecionada(cbP2, GROUP2)));
            if (lblP3 != null) listaRespostas.add(new Resposta("P3", lblP3.getText(), obterOpcaoSelecionada(cbP3, GROUP3)));
            if (lblP4 != null) listaRespostas.add(new Resposta("P4", lblP4.getText(), obterOpcaoSelecionada(cbP4, GROUP4)));

            for (Resposta r : listaRespostas) {
                if ("Sem resposta".equals(r.getOpcaoEscolhida())) {
                    throw new ColetaIncompletaException("Por favor, responda a todas as perguntas antes de finalizar!");
                }
            }

            String idEntrevista = "ENT" + (System.currentTimeMillis() % 10000);
            String codigoPesquisa = (cbPesquisaPertencente != null && cbPesquisaPertencente.getValue() != null)
                    ? cbPesquisaPertencente.getValue()
                    : "PESQ001";

            Entrevista entrevistaCompleta = new Entrevista(
                    idEntrevista,
                    codigoPesquisa,
                    entrevistador,
                    participante,
                    listaRespostas
            );

            coletaService.salvarEntrevista(entrevistaCompleta);

            exibirAlerta("Sucesso", "Entrevista gravada com sucesso no arquivo!", Alert.AlertType.INFORMATION);
            voltarParaMenu(event);

        } catch (EntrevistadorInvalidoException | AmostraInvalidaException | ColetaIncompletaException e) {
            exibirAlerta("Erro", e.getMessage(), Alert.AlertType.WARNING);
        }
    }
}