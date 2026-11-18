package org.example.viewControllers.ColetaController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import org.example.models.Usuario;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.ColetaServiceController;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.PesquisaController;
import org.example.utils.Sessao;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    @FXML private TextField txtCodigoParticipante;

    // Gênero não é parâmetro do Admin; mesma lista usada nos filtros da Análise
    private static final List<String> GENEROS = List.of("Masculino", "Feminino", "Outro");

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

    @FXML private ComboBox<Pesquisa> cbPesquisaPertencente;
    @FXML private DatePicker dpDataEntrevista;

    // Campos da tela FiltroEntrevistas.fxml
    @FXML private ComboBox<String> cbFiltroEscolaridade;
    @FXML private ComboBox<String> cbFiltroGenero;
    @FXML private ComboBox<String> cbFiltroRegiao;
    @FXML private ComboBox<String> cbFiltroFaixaRenda;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Preenche o entrevistador com o usuário logado (o e-mail serve como código, já que Usuario não tem um)
        Usuario logado = Sessao.getUsuarioLogado();
        if (logado != null) {
            if (txtCodigoEntrevistador != null && txtCodigoEntrevistador.getText().isEmpty()) txtCodigoEntrevistador.setText(logado.getEmail());
            if (txtNomeEntrevistador != null && txtNomeEntrevistador.getText().isEmpty()) txtNomeEntrevistador.setText(logado.getNome());
        }

        // As características da amostra vêm dos parâmetros cadastrados pelo Admin (parametros.dat),
        // os mesmos que a Análise usa nos filtros, para os valores gravados baterem.
        ParametroController parametros = new ParametroController();

        // Popula Amostra
        if (cbFaixaEtaria != null) cbFaixaEtaria.getItems().addAll(parametros.listarFaixasEtarias());
        if (cbEscolaridade != null) cbEscolaridade.getItems().addAll(parametros.listarEscolaridades());
        if (cbGenero != null) cbGenero.getItems().addAll(GENEROS);
        if (cbRegiao != null) cbRegiao.getItems().addAll(parametros.listarRegioes());
        if (cbFaixaRenda != null) cbFaixaRenda.getItems().addAll(parametros.listarFaixasRenda());

        // Popula Filtro de Entrevistas
        if (cbFiltroEscolaridade != null) { cbFiltroEscolaridade.getItems().add("Todas"); cbFiltroEscolaridade.getItems().addAll(parametros.listarEscolaridades()); }
        if (cbFiltroGenero != null) { cbFiltroGenero.getItems().add("Todos"); cbFiltroGenero.getItems().addAll(GENEROS); }
        if (cbFiltroRegiao != null) { cbFiltroRegiao.getItems().add("Todas"); cbFiltroRegiao.getItems().addAll(parametros.listarRegioes()); }
        if (cbFiltroFaixaRenda != null) { cbFiltroFaixaRenda.getItems().add("Todas"); cbFiltroFaixaRenda.getItems().addAll(parametros.listarFaixasRenda()); }

        // Popula Registro de Pesquisa
        if (cbP1 != null) cbP1.getItems().addAll("Candidato A", "Candidato B", "Candidato C");
        if (cbP2 != null) cbP2.getItems().addAll("Candidato A", "Candidato B", "Candidato C");
        if (cbP3 != null) cbP3.getItems().addAll("Sim", "Não", "Parcialmente");
        if (cbP4 != null) cbP4.getItems().addAll("Sim", "Não");
        if (cbPesquisaPertencente != null) carregarPesquisasDisponiveis();
        // Data da entrevista: começa em hoje, mas pode ser alterada (ex.: registrar entrevistas de outros dias)
        if (dpDataEntrevista != null) dpDataEntrevista.setValue(LocalDate.now());
    }

    // Busca as pesquisas cadastradas pelo módulo de Pesquisas (arquivo pesquisas_data.txt).
    // Um novo PesquisaController é criado a cada abertura da tela para reler o arquivo
    // e enxergar pesquisas criadas depois que o programa iniciou.
    private void carregarPesquisasDisponiveis() {
        cbPesquisaPertencente.getItems().clear();
        PesquisaController pesquisaController = new PesquisaController();
        try {
            for (Pesquisa p : pesquisaController.listarTodas()) {
                // Regra de negócio: pesquisa encerrada/processada não recebe novas entrevistas
                if (pesquisaController.podeReceberEntrevista(p.getCodigo())) {
                    cbPesquisaPertencente.getItems().add(p);
                }
            }
        } catch (PesquisaInvalidaException e) {
            // Nenhuma pesquisa cadastrada: o combo fica vazio e o aviso aparece ao finalizar
        }
        if (cbPesquisaPertencente.getItems().isEmpty()) {
            cbPesquisaPertencente.setPromptText("Nenhuma pesquisa disponível");
        }
    }

    // Lê a tela de Configuração da Amostra, valida e guarda no service para a tela de respostas usar.
    private void salvarAmostraAtual() throws EntrevistadorInvalidoException, AmostraInvalidaException {
        String codigoEntrevistador = textoDe(txtCodigoEntrevistador);
        String nomeEntrevistador = textoDe(txtNomeEntrevistador);
        if (codigoEntrevistador.isEmpty() || nomeEntrevistador.isEmpty()) {
            throw new EntrevistadorInvalidoException("Informe o código e o nome do entrevistador.");
        }

        String codigoParticipante = textoDe(txtCodigoParticipante);
        if (codigoParticipante.isEmpty()) {
            throw new AmostraInvalidaException("Informe o código do participante.");
        }

        validarSelecao(cbFaixaEtaria, "faixa etária");
        validarSelecao(cbEscolaridade, "escolaridade");
        validarSelecao(cbGenero, "gênero");
        validarSelecao(cbRegiao, "região");
        validarSelecao(cbFaixaRenda, "faixa de renda");

        Entrevistador entrevistador = new Entrevistador(codigoEntrevistador, nomeEntrevistador);
        Participante participante = new Participante(
                codigoParticipante,
                cbFaixaEtaria.getValue(),
                cbEscolaridade.getValue(),
                cbGenero.getValue(),
                cbRegiao.getValue(),
                cbFaixaRenda.getValue()
        );

        coletaService.salvarConfiguracaoAmostra(entrevistador, participante);
    }

    private String textoDe(TextField campo) {
        return (campo != null && campo.getText() != null) ? campo.getText().trim() : "";
    }

    private void validarSelecao(ComboBox<String> combo, String nomeCampo) throws AmostraInvalidaException {
        if (combo == null || combo.getValue() == null || combo.getValue().trim().isEmpty()) {
            throw new AmostraInvalidaException("Selecione a " + nomeCampo + " do participante.");
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
        try {
            salvarAmostraAtual();
            exibirAlerta("Amostra", "Configuração da amostra guardada.", Alert.AlertType.INFORMATION);
            voltarParaMenu(event);
        } catch (EntrevistadorInvalidoException | AmostraInvalidaException e) {
            exibirAlerta("Amostra incompleta", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void realizarPesquisa(ActionEvent event) throws IOException {
        voltarParaMenu(event);
    }

    @FXML
    public void ProxPesquisa(ActionEvent event) throws IOException {
        try {
            salvarAmostraAtual();
        } catch (EntrevistadorInvalidoException | AmostraInvalidaException e) {
            exibirAlerta("Amostra incompleta", e.getMessage(), Alert.AlertType.WARNING);
            return; // fica na tela para o usuário completar
        }

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

            // Toda entrevista deve estar vinculada a uma pesquisa existente
            Pesquisa pesquisaSelecionada = (cbPesquisaPertencente != null) ? cbPesquisaPertencente.getValue() : null;
            if (pesquisaSelecionada == null) {
                throw new ColetaIncompletaException("Selecione a pesquisa à qual esta entrevista pertence!");
            }

            LocalDate dataEntrevista = (dpDataEntrevista != null) ? dpDataEntrevista.getValue() : LocalDate.now();
            if (dataEntrevista == null) {
                throw new ColetaIncompletaException("Informe a data da entrevista!");
            }
            if (dataEntrevista.isAfter(LocalDate.now())) {
                throw new ColetaIncompletaException("A data da entrevista não pode estar no futuro!");
            }

            String idEntrevista = "ENT" + (System.currentTimeMillis() % 10000);
            String codigoPesquisa = pesquisaSelecionada.getCodigo();

            Entrevista entrevistaCompleta = new Entrevista(
                    idEntrevista,
                    codigoPesquisa,
                    entrevistador,
                    participante,
                    listaRespostas
            );
            entrevistaCompleta.setData(dataEntrevista);

            coletaService.salvarEntrevista(entrevistaCompleta);

            exibirAlerta("Sucesso", "Entrevista gravada com sucesso no arquivo!", Alert.AlertType.INFORMATION);
            voltarParaMenu(event);

        } catch (EntrevistadorInvalidoException | AmostraInvalidaException | ColetaIncompletaException e) {
            exibirAlerta("Erro", e.getMessage(), Alert.AlertType.WARNING);
        }
    }
}