package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.exceptions.AmostraInvalidaException;
import org.example.exceptions.ColetaIncompletaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.Entrevista;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.models.coleta.Resposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.PesquisaController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//passo 2 da entrevista: escolhe a pesquisa e responde as perguntas dela
public class RegistroRespostasController extends TelaColeta implements Initializable {

    @FXML private ComboBox<Pesquisa> cbPesquisa;
    @FXML private TextField txtCodigoEntrevista;
    @FXML private DatePicker dpDataEntrevista;
    @FXML private Label lblEntrevistador;
    @FXML private Label lblParticipante;
    @FXML private VBox boxPerguntas;

    private final List<CampoResposta> campos = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mostrarResumoAmostra();
        dpDataEntrevista.setValue(LocalDate.now());
        carregarPesquisas();
        cbPesquisa.valueProperty().addListener((obs, antiga, nova) -> trocarPesquisa(nova));
    }

    private void mostrarResumoAmostra() {
        Entrevistador entrevistador = coleta.getEntrevistadorAtual();
        Participante participante = coleta.getParticipanteAtual();
        lblEntrevistador.setText(entrevistador == null ? "-" : entrevistador.getNome() + " (" + entrevistador.getCodigo() + ")");
        lblParticipante.setText(participante == null ? "-" : participante.getCodigo() + "  •  " + participante.getRegiao() + "  •  " + participante.getFaixaEtaria()
                + "  •  " + participante.getGenero() + "  •  " + participante.getEscolaridade() + "  •  " + participante.getFaixaRenda());
    }

    //so entram pesquisas que ainda aceitam entrevista (nao encerradas/processadas)
    private void carregarPesquisas() {
        PesquisaController pesquisas = new PesquisaController();
        try {
            for (Pesquisa pesquisa : pesquisas.listarTodas()) {
                if (pesquisas.podeReceberEntrevista(pesquisa.getCodigo())) {
                    cbPesquisa.getItems().add(pesquisa);
                }
            }
        } catch (PesquisaInvalidaException erro) {
            //nenhuma pesquisa cadastrada, o combo fica vazio
        }
        if (cbPesquisa.getItems().isEmpty()) {
            cbPesquisa.setPromptText("Nenhuma pesquisa em coleta");
        }
    }

    private void trocarPesquisa(Pesquisa pesquisa) {
        boxPerguntas.getChildren().clear();
        campos.clear();

        if (pesquisa == null) {
            boxPerguntas.getChildren().add(new Label("Selecione uma pesquisa para carregar as perguntas."));
            return;
        }
        txtCodigoEntrevista.setText(coleta.proximoCodigoEntrevista(pesquisa.getCodigo()));

        List<Pergunta> perguntas = pesquisa.getPerguntas();
        if (perguntas == null || perguntas.isEmpty()) {
            boxPerguntas.getChildren().add(new Label("Essa pesquisa ainda não tem perguntas cadastradas."));
            return;
        }
        int numero = 1;
        for (Pergunta pergunta : perguntas) {
            CampoResposta campo = FabricResp.criar(pergunta);
            campos.add(campo);
            boxPerguntas.getChildren().add(campo.montar(numero++));
        }
    }

    @FXML
    public void alterarAmostra(ActionEvent event) throws IOException {
        trocarTela(event, CONFIG_AMOSTRA);
    }

    @FXML
    public void salvarEntrevista(ActionEvent event) throws IOException {
        Pesquisa pesquisa = cbPesquisa.getValue();
        try {
            Entrevista entrevista = montarEntrevista(pesquisa);
            coleta.registrarEntrevista(entrevista, pesquisa);

            boolean outra = confirmar("Entrevista " + entrevista.getIdEntrevista() + " registrada na pesquisa \""
                    + pesquisa.getNome() + "\".\n\nQuer registrar outra entrevista?");
            trocarTela(event, outra ? CONFIG_AMOSTRA : MENU_COLETA);
        } catch (ColetaIncompletaException | AmostraInvalidaException erro) {
            mostrarAviso(erro.getMessage());
        }
    }

    private Entrevista montarEntrevista(Pesquisa pesquisa) throws ColetaIncompletaException, AmostraInvalidaException {
        if (!coleta.temAmostraConfigurada()) {
            throw new AmostraInvalidaException("Configure a amostra antes de registrar as respostas.");
        }
        if (pesquisa == null) {
            throw new ColetaIncompletaException("Selecione a pesquisa da entrevista.");
        }
        if (campos.isEmpty()) {
            throw new ColetaIncompletaException("Essa pesquisa não tem perguntas para responder.");
        }

        String codigo = texto(txtCodigoEntrevista).toUpperCase();
        if (codigo.isEmpty()) {
            throw new ColetaIncompletaException("Informe o código da entrevista.");
        }

        LocalDate data = dpDataEntrevista.getValue();
        if (data == null) {
            throw new ColetaIncompletaException("Informe a data da entrevista.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new ColetaIncompletaException("A data da entrevista não pode ser no futuro.");
        }

        List<Resposta> respostas = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            Resposta resp = campos.get(i).getResposta();
            if (resp == null) {
                throw new ColetaIncompletaException("Falta responder a pergunta " + (i + 1) + ".");
            }
            respostas.add(resp);
        }

        Entrevista entrevista = new Entrevista(codigo, pesquisa.getCodigo(),
                coleta.getEntrevistadorAtual(), coleta.getParticipanteAtual(), respostas);
        entrevista.setData(data);
        return entrevista;
    }
}
