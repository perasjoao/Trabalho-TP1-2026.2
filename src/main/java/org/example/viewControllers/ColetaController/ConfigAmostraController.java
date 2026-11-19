package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.exceptions.AmostraInvalidaException;
import org.example.exceptions.EntrevistadorInvalidoException;
import org.example.models.Usuario;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.serviceControllers.ParametroController;
import org.example.utils.Sessao;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//passo 1 da entrevista: quem entrevista e o perfil de quem responde
public class ConfigAmostraController extends TelaColeta implements Initializable {

    @FXML private TextField txtCodigoEntrevistador;
    @FXML private TextField txtNomeEntrevistador;
    @FXML private TextField txtCodigoParticipante;
    @FXML private ComboBox<String> cbFaixaEtaria;
    @FXML private ComboBox<String> cbEscolaridade;
    @FXML private ComboBox<String> cbGenero;
    @FXML private ComboBox<String> cbRegiao;
    @FXML private ComboBox<String> cbFaixaRenda;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //opcoes vem dos parametros do admin, os mesmos que a analise usa nos filtros
        ParametroController parametros = new ParametroController();
        cbFaixaEtaria.getItems().addAll(parametros.listarFaixasEtarias());
        cbEscolaridade.getItems().addAll(parametros.listarEscolaridades());
        cbGenero.getItems().addAll(GENEROS);
        cbRegiao.getItems().addAll(parametros.listarRegioes());
        cbFaixaRenda.getItems().addAll(parametros.listarFaixasRenda());

        preencherEntrevistador();
        preencherParticipante();
    }

    //mantem o entrevistador da ultima entrevista; se nao tiver, usa o usuario logado
    private void preencherEntrevistador() {
        Entrevistador atual = coleta.getEntrevistadorAtual();
        if (atual != null) {
            txtCodigoEntrevistador.setText(atual.getCodigo());
            txtNomeEntrevistador.setText(atual.getNome());
            return;
        }
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null) {
            txtCodigoEntrevistador.setText(logado.getEmail());
            txtNomeEntrevistador.setText(logado.getNome());
        }
    }

    //se o usuario voltou da tela de respostas, os dados ainda estao aqui
    private void preencherParticipante() {
        Participante participante = coleta.getParticipanteAtual();
        if (participante == null) return;
        txtCodigoParticipante.setText(participante.getCodigo());
        cbFaixaEtaria.setValue(participante.getFaixaEtaria());
        cbEscolaridade.setValue(participante.getEscolaridade());
        cbGenero.setValue(participante.getGenero());
        cbRegiao.setValue(participante.getRegiao());
        cbFaixaRenda.setValue(participante.getFaixaRenda());
    }

    @FXML
    public void continuarParaRespostas(ActionEvent event) throws IOException {
        try {
            guardarAmostra();
            trocarTela(event, REGISTRO_RESPOSTAS);
        } catch (EntrevistadorInvalidoException | AmostraInvalidaException erro) {
            mostrarAviso(erro.getMessage());
        }
    }

    private void guardarAmostra() throws EntrevistadorInvalidoException, AmostraInvalidaException {
        String codigoEntrevistador = texto(txtCodigoEntrevistador);
        String nomeEntrevistador = texto(txtNomeEntrevistador);
        if (codigoEntrevistador.isEmpty() || nomeEntrevistador.isEmpty()) {
            throw new EntrevistadorInvalidoException("Informe o código e o nome do entrevistador.");
        }

        String codigoParticipante = texto(txtCodigoParticipante);
        if (codigoParticipante.isEmpty()) {
            throw new AmostraInvalidaException("Informe o código do participante.");
        }
        exigir(cbFaixaEtaria, "a faixa etária");
        exigir(cbEscolaridade, "a escolaridade");
        exigir(cbGenero, "o gênero");
        exigir(cbRegiao, "a região");
        exigir(cbFaixaRenda, "a faixa de renda");

        Entrevistador entrevistador = new Entrevistador(codigoEntrevistador, nomeEntrevistador);
        Participante participante = new Participante(codigoParticipante, cbFaixaEtaria.getValue(),
                cbEscolaridade.getValue(), cbGenero.getValue(), cbRegiao.getValue(), cbFaixaRenda.getValue());
        coleta.salvarConfiguracaoAmostra(entrevistador, participante);
    }

    private void exigir(ComboBox<String> combo, String nome) throws AmostraInvalidaException {
        if (combo.getValue() == null || combo.getValue().isBlank()) {
            throw new AmostraInvalidaException("Selecione " + nome + " do participante.");
        }
    }
}
