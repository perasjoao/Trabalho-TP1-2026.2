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
import javafx.scene.control.Toggle;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.ColetaServiceController;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.PesquisaController;
import org.example.utils.Sessao;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

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
    @FXML private VBox boxPerguntas;
    @FXML private ComboBox<Pesquisa> cbPesquisaPertencente;

    // Respostas especiais aceitas em qualquer pergunta (contabilizadas à parte na Análise)
    private static final List<String> RESPOSTAS_ESPECIAIS = List.of("Não sabe", "Não respondeu", "Nenhuma das opções");
    private static final String TIPO_RESPOSTA_LIVRE = "Resposta Livre";

    // Para cada pergunta exibida, guarda como ler a resposta escolhida (null = não respondida)
    private final Map<Pergunta, Supplier<String>> leitoresDeResposta = new LinkedHashMap<>();
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

        // Popula Registro de Respostas: ao escolher a pesquisa, monta as perguntas dela
        if (cbPesquisaPertencente != null) {
            carregarPesquisasDisponiveis();
            cbPesquisaPertencente.valueProperty().addListener((obs, antiga, nova) -> montarPerguntas(nova));
        }
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

    // Monta na tela as perguntas cadastradas na pesquisa escolhida (módulo de Pesquisas).
    private void montarPerguntas(Pesquisa pesquisa) {
        boxPerguntas.getChildren().clear();
        leitoresDeResposta.clear();

        if (pesquisa == null) {
            boxPerguntas.getChildren().add(new Label("Selecione uma pesquisa para carregar as perguntas."));
            return;
        }
        List<Pergunta> perguntas = pesquisa.getPerguntas();
        if (perguntas == null || perguntas.isEmpty()) {
            boxPerguntas.getChildren().add(new Label("Esta pesquisa ainda não possui perguntas cadastradas."));
            return;
        }

        int numero = 1;
        for (Pergunta pergunta : perguntas) {
            Label enunciado = new Label(numero++ + ". " + pergunta.getEnunciado());
            enunciado.setWrapText(true);
            enunciado.setStyle("-fx-font-weight: bold;");

            // Um único ToggleGroup por pergunta: só uma resposta pode ficar marcada
            ToggleGroup grupo = new ToggleGroup();
            FlowPane opcoes = new FlowPane(15, 6);

            TextField campoLivre = null;
            if (TIPO_RESPOSTA_LIVRE.equalsIgnoreCase(pergunta.getTipo())) {
                campoLivre = new TextField();
                campoLivre.setPromptText("Digite a resposta");
                campoLivre.setPrefWidth(250);
                // Digitar uma resposta desmarca "Não sabe"/etc., e vice-versa
                TextField campo = campoLivre;
                campo.textProperty().addListener((o, a, n) -> {
                    if (n != null && !n.isBlank()) grupo.selectToggle(null);
                });
                grupo.selectedToggleProperty().addListener((o, a, n) -> {
                    if (n != null) campo.clear();
                });
                opcoes.getChildren().add(campo);
            } else {
                for (OpcaoResposta opcao : pergunta.getOpcoes()) {
                    opcoes.getChildren().add(criarRadio(opcao.getTexto(), grupo));
                }
            }
            for (String especial : RESPOSTAS_ESPECIAIS) {
                opcoes.getChildren().add(criarRadio(especial, grupo));
            }

            TextField campoFinal = campoLivre;
            leitoresDeResposta.put(pergunta, () -> {
                Toggle marcado = grupo.getSelectedToggle();
                if (marcado != null) return ((RadioButton) marcado).getText();
                if (campoFinal != null && !campoFinal.getText().isBlank()) return campoFinal.getText().trim();
                return null;
            });

            boxPerguntas.getChildren().add(new VBox(4, enunciado, opcoes));
        }
    }

    private RadioButton criarRadio(String texto, ToggleGroup grupo) {
        RadioButton rb = new RadioButton(texto);
        rb.setToggleGroup(grupo);
        return rb;
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

            // 3. Toda entrevista deve estar vinculada a uma pesquisa existente
            Pesquisa pesquisaSelecionada = (cbPesquisaPertencente != null) ? cbPesquisaPertencente.getValue() : null;
            if (pesquisaSelecionada == null) {
                throw new ColetaIncompletaException("Selecione a pesquisa à qual esta entrevista pertence!");
            }
            if (leitoresDeResposta.isEmpty()) {
                throw new ColetaIncompletaException("A pesquisa selecionada não possui perguntas cadastradas.");
            }

            // 4. Uma única resposta para cada pergunta da pesquisa
            List<Resposta> listaRespostas = new ArrayList<>();
            int numero = 1;
            for (Map.Entry<Pergunta, Supplier<String>> item : leitoresDeResposta.entrySet()) {
                Pergunta pergunta = item.getKey();
                String escolhida = item.getValue().get();
                if (escolhida == null) {
                    throw new ColetaIncompletaException("Responda a pergunta " + numero + " antes de finalizar!");
                }
                listaRespostas.add(new Resposta(pergunta.getId(), pergunta.getEnunciado(), escolhida));
                numero++;
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