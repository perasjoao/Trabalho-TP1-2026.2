package org.example.viewControllers.ResearchController;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.candidatos.Candidatura;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PlanoAmostral;
import org.example.serviceControllers.ParametroController;
import org.example.serviceControllers.candidatos.CandidatoController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurarPesquisaController {

    private final CandidatoController candidatoController = new CandidatoController();
    private final ParametroController parametroController = new ParametroController();

    @FXML private ComboBox<Pesquisa> cbPesquisa;

    // perguntas
    @FXML private TextField txtEnunciadoPergunta;
    @FXML private ComboBox<String> cbTipoPergunta;
    @FXML private TextField txtNovaOpcao;
    @FXML private ListView<OpcaoResposta> listOpcoes;
    @FXML private CheckBox chkTodosCandidatos;
    @FXML private VBox boxCheckboxesCandidatos;
    @FXML private TableView<Pergunta> tblPerguntas;
    @FXML private TableColumn<Pergunta, String> colNumPergunta;
    @FXML private TableColumn<Pergunta, String> colTextoPergunta;
    @FXML private TableColumn<Pergunta, String> colTipoPergunta;
    @FXML private TableColumn<Pergunta, String> colOpcoesPergunta;
    @FXML private TableColumn<Pergunta, String> colCandidatosPergunta;

    // candidatos
    @FXML private ListView<Candidatura> listCandidatosDisponiveis;
    @FXML private ListView<Candidatura> listCandidatosAssociados;

    // plano amostral
    @FXML private Label lblResumoPrevistas;
    @FXML private ComboBox<String> cbCategoriaMeta;
    @FXML private ComboBox<String> cbGrupoMeta;
    @FXML private TextField txtQtdMinimaMeta;
    @FXML private Label lblSomaCategoria;
    @FXML private TableView<MetaAmostralItem> tblMetasAmostrais;
    @FXML private TableColumn<MetaAmostralItem, String> colCategoriaMeta;
    @FXML private TableColumn<MetaAmostralItem, String> colGrupoMeta;
    @FXML private TableColumn<MetaAmostralItem, Number> colQtdMinimaMeta;
    @FXML private TableColumn<MetaAmostralItem, String> colSomaCategoriaMeta;

    private Pesquisa pesquisaAtual;
    private final ObservableList<OpcaoResposta> opcoesEmEdicao = FXCollections.observableArrayList();
    private final Map<Candidatura, CheckBox> mapaCheckboxesCandidatos = new LinkedHashMap<>();
    private boolean atualizandoCheckboxesCandidatos = false;

    @FXML
    public void initialize() {
        cbPesquisa.setItems(FXCollections.observableArrayList(
                GerenciarPesquisasController.getPesquisaController().getPesquisas()
        ));

        cbTipoPergunta.setItems(FXCollections.observableArrayList(
                "Única Escolha", "Múltipla Escolha", "Escala", "Resposta Livre"
        ));

        listOpcoes.setItems(opcoesEmEdicao);

        configurarCellFactoryCandidatura(listCandidatosDisponiveis);
        configurarCellFactoryCandidatura(listCandidatosAssociados);

        colNumPergunta.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTextoPergunta.setCellValueFactory(new PropertyValueFactory<>("enunciado"));
        colTipoPergunta.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colOpcoesPergunta.setCellValueFactory(cellData -> {
            List<OpcaoResposta> ops = cellData.getValue().getOpcoes();
            if (ops == null || ops.isEmpty()) {
                return new SimpleStringProperty("Sem opções");
            }
            String resumo = ops.stream()
                    .map(OpcaoResposta::getTexto)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(resumo);
        });
        colCandidatosPergunta.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getResumoCandidatos()));

        tblPerguntas.getSelectionModel().selectedItemProperty().addListener((obs, antiga, selecionada) -> {
            if (selecionada != null) {
                txtEnunciadoPergunta.setText(selecionada.getEnunciado());
                cbTipoPergunta.setValue(selecionada.getTipo());
                opcoesEmEdicao.setAll(selecionada.getOpcoes() != null ? selecionada.getOpcoes() : new ArrayList<>());
                preencherCheckboxesCandidatosDaPergunta(selecionada);
            }
        });

        // config plano amostral
        cbCategoriaMeta.setItems(FXCollections.observableArrayList(
                "Região", "Faixa etária", "Escolaridade", "Faixa de renda", "Gênero"
        ));

        colCategoriaMeta.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().categoria));
        colGrupoMeta.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().grupo));
        colQtdMinimaMeta.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().quantidadeMinima));
        colSomaCategoriaMeta.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().resumoCategoria));

        tblMetasAmostrais.getSelectionModel().selectedItemProperty().addListener((obs, antiga, selecionada) -> {
            if (selecionada != null) {
                cbCategoriaMeta.setValue(selecionada.categoria);
                carregarGruposDaCategoria(selecionada.categoria);
                cbGrupoMeta.setValue(selecionada.grupo);
                txtQtdMinimaMeta.setText(String.valueOf(selecionada.quantidadeMinima));
                atualizarLabelSomaCategoria();
            }
        });
    }

    private void configurarCellFactoryCandidatura(ListView<Candidatura> listView) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Candidatura item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getDescricao());
            }
        });
    }

    public void setPesquisaConfiguracao(Pesquisa pesquisa) {
        this.pesquisaAtual = pesquisa;
        if (pesquisa != null) {
            cbPesquisa.setValue(pesquisa);
            atualizarTodasAsAbas();
        }
    }

    @FXML
    public void aoSelecionarPesquisa(ActionEvent event) {
        Pesquisa selecionada = cbPesquisa.getValue();
        if (selecionada != null) {
            this.pesquisaAtual = selecionada;
            atualizarTodasAsAbas();
        }
    }

    private void atualizarTodasAsAbas() {
        if (pesquisaAtual == null) return;


        List<Candidatura> todasCandidaturas = candidatoController.listar();
        GerenciarPesquisasController.getPesquisaController().vincularCandidaturasCarregadas(todasCandidaturas);


        reconstruirCheckboxesCandidatos();


        tblPerguntas.setItems(FXCollections.observableArrayList(pesquisaAtual.getPerguntas()));
        limparFormularioPergunta();


        List<Candidatura> candAssociadas = pesquisaAtual.getCandidaturasAssociadas();
        List<Candidatura> candDisponiveis = todasCandidaturas.stream()
                .filter(c -> c.getCargo() == pesquisaAtual.getCargo())
                .filter(c -> candAssociadas.stream().noneMatch(a -> a.getNumero() == c.getNumero()))
                .collect(Collectors.toList());

        listCandidatosDisponiveis.setItems(FXCollections.observableArrayList(candDisponiveis));
        listCandidatosAssociados.setItems(FXCollections.observableArrayList(candAssociadas));


        atualizarTabelaPlanoAmostral();
    }

    private void reconstruirCheckboxesCandidatos() {
        boxCheckboxesCandidatos.getChildren().clear();
        mapaCheckboxesCandidatos.clear();

        List<Candidatura> associadas = pesquisaAtual.getCandidaturasAssociadas();
        if (associadas == null || associadas.isEmpty()) {
            Label aviso = new Label("Associe candidatos na aba 'Candidatos'.");
            aviso.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
            boxCheckboxesCandidatos.getChildren().add(aviso);
            return;
        }

        for (Candidatura c : associadas) {
            CheckBox chk = new CheckBox(c.getDescricao());
            chk.selectedProperty().addListener((obs, antes, agora) -> {
                if (atualizandoCheckboxesCandidatos) return;
                atualizandoCheckboxesCandidatos = true;
                boolean todosMarcados = !mapaCheckboxesCandidatos.isEmpty()
                        && mapaCheckboxesCandidatos.values().stream().allMatch(CheckBox::isSelected);
                chkTodosCandidatos.setSelected(todosMarcados);
                atualizandoCheckboxesCandidatos = false;
            });
            mapaCheckboxesCandidatos.put(c, chk);
            boxCheckboxesCandidatos.getChildren().add(chk);
        }
    }

    @FXML
    public void aoAlterarTodosCandidatos(ActionEvent event) {
        if (atualizandoCheckboxesCandidatos) return;
        atualizandoCheckboxesCandidatos = true;
        boolean marcar = chkTodosCandidatos.isSelected();
        for (CheckBox chk : mapaCheckboxesCandidatos.values()) {
            chk.setSelected(marcar);
        }
        atualizandoCheckboxesCandidatos = false;
    }

    private void preencherCheckboxesCandidatosDaPergunta(Pergunta pergunta) {
        atualizandoCheckboxesCandidatos = true;
        if (pergunta.isParaTodosCandidatos()) {
            chkTodosCandidatos.setSelected(true);
            for (CheckBox chk : mapaCheckboxesCandidatos.values()) {
                chk.setSelected(true);
            }
        } else {
            chkTodosCandidatos.setSelected(false);
            for (Map.Entry<Candidatura, CheckBox> entry : mapaCheckboxesCandidatos.entrySet()) {
                boolean marcado = pergunta.refereSeAoCandidato(entry.getKey());
                entry.getValue().setSelected(marcado);
            }
        }
        atualizandoCheckboxesCandidatos = false;
    }

    private List<Candidatura> obterCandidaturasSelecionadasNaPergunta() {
        List<Candidatura> selecionadas = new ArrayList<>();
        for (Map.Entry<Candidatura, CheckBox> entry : mapaCheckboxesCandidatos.entrySet()) {
            if (entry.getValue().isSelected()) {
                selecionadas.add(entry.getKey());
            }
        }
        return selecionadas;
    }

    // aba perguntas

    @FXML
    public void adicionarOpcao(ActionEvent event) {
        String texto = txtNovaOpcao.getText();
        if (texto == null || texto.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Digite o texto da opção de resposta.");
            return;
        }
        int numero = opcoesEmEdicao.size() + 1;
        opcoesEmEdicao.add(new OpcaoResposta(numero, texto.trim()));
        txtNovaOpcao.clear();
    }

    @FXML
    public void removerOpcao(ActionEvent event) {
        OpcaoResposta selecionada = listOpcoes.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            opcoesEmEdicao.remove(selecionada);
            for (int i = 0; i < opcoesEmEdicao.size(); i++) {
                opcoesEmEdicao.get(i).setNumero(i + 1);
            }
            listOpcoes.refresh();
        }
    }

    @FXML
    public void adicionarPergunta(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        try {
            String enunciado = txtEnunciadoPergunta.getText();
            String tipo = cbTipoPergunta.getValue();
            String id = "Q" + (pesquisaAtual.getPerguntas().size() + 1);
            while (existeIdPergunta(id)) {
                id = "Q" + (Integer.parseInt(id.substring(1)) + 1);
            }

            Pergunta nova = new Pergunta(id, enunciado, tipo, new ArrayList<>(opcoesEmEdicao));
            nova.setParaTodosCandidatos(chkTodosCandidatos.isSelected());
            nova.setCandidaturasAssociadas(obterCandidaturasSelecionadasNaPergunta());

            GerenciarPesquisasController.getPesquisaController().adicionarPergunta(pesquisaAtual.getCodigo(), nova);
            atualizarTodasAsAbas();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    public void editarPergunta(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        Pergunta selecionada = tblPerguntas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pergunta na tabela para editar.");
            return;
        }
        try {
            GerenciarPesquisasController.getPesquisaController().editarPergunta(
                    pesquisaAtual.getCodigo(),
                    selecionada.getId(),
                    txtEnunciadoPergunta.getText(),
                    cbTipoPergunta.getValue(),
                    new ArrayList<>(opcoesEmEdicao),
                    chkTodosCandidatos.isSelected(),
                    obterCandidaturasSelecionadasNaPergunta()
            );
            atualizarTodasAsAbas();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    public void excluirPergunta(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        Pergunta selecionada = tblPerguntas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pergunta na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir pergunta " + selecionada.getId() + "?");
        confirmacao.setContentText("\"" + selecionada.getEnunciado() + "\"\n\nEsta ação não poderá ser desfeita.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                GerenciarPesquisasController.getPesquisaController().removerPergunta(pesquisaAtual.getCodigo(), selecionada.getId());
                atualizarTodasAsAbas();
            } catch (PesquisaInvalidaException | PersistenciaException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
            }
        }
    }

    private boolean existeIdPergunta(String id) {
        return pesquisaAtual.getPerguntas().stream().anyMatch(q -> q.getId().equalsIgnoreCase(id));
    }

    private void limparFormularioPergunta() {
        txtEnunciadoPergunta.clear();
        cbTipoPergunta.setValue(null);
        txtNovaOpcao.clear();
        opcoesEmEdicao.clear();

        atualizandoCheckboxesCandidatos = true;
        chkTodosCandidatos.setSelected(false);
        for (CheckBox chk : mapaCheckboxesCandidatos.values()) {
            chk.setSelected(false);
        }
        atualizandoCheckboxesCandidatos = false;
    }

    // aba candidatos

    @FXML
    public void associarCandidato(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        Candidatura selecionada = listCandidatosDisponiveis.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um candidato disponível para associar.");
            return;
        }
        try {
            GerenciarPesquisasController.getPesquisaController().associarCandidatura(pesquisaAtual.getCodigo(), selecionada);
            atualizarTodasAsAbas();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    @FXML
    public void removerCandidato(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        Candidatura selecionada = listCandidatosAssociados.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um candidato associado para remover.");
            return;
        }
        try {
            GerenciarPesquisasController.getPesquisaController().removerCandidatura(pesquisaAtual.getCodigo(), selecionada);
            atualizarTodasAsAbas();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    // aba plano

    @FXML
    public void aoSelecionarCategoriaMeta(ActionEvent event) {
        String categoria = cbCategoriaMeta.getValue();
        carregarGruposDaCategoria(categoria);
        atualizarLabelSomaCategoria();
    }

    private void carregarGruposDaCategoria(String categoria) {
        cbGrupoMeta.getItems().clear();
        cbGrupoMeta.setValue(null);
        if (categoria == null) return;

        List<String> opcoes;
        switch (categoria) {
            case "Região":
                opcoes = (pesquisaAtual != null && pesquisaAtual.getRegioesAssociadas() != null)
                        ? pesquisaAtual.getRegioesAssociadas()
                        : Collections.emptyList();
                break;
            case "Faixa etária":
                opcoes = parametroController.listarFaixasEtarias();
                break;
            case "Escolaridade":
                opcoes = parametroController.listarEscolaridades();
                break;
            case "Faixa de renda":
                opcoes = parametroController.listarFaixasRenda();
                break;
            case "Gênero":
                opcoes = Arrays.asList("Masculino", "Feminino", "Outro");
                break;
            default:
                opcoes = Collections.emptyList();
        }

        if (opcoes != null && !opcoes.isEmpty()) {
            cbGrupoMeta.setItems(FXCollections.observableArrayList(opcoes));
        } else {
            String msg = "Região".equals(categoria)
                    ? "Nenhuma região está associada a esta pesquisa."
                    : "Nenhum item cadastrado pelo Administrador na categoria '" + categoria + "'.";
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sem opções disponíveis", msg);
        }
    }

    @FXML
    public void definirMetaAmostral(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;

        String categoria = cbCategoriaMeta.getValue();
        String grupo = cbGrupoMeta.getValue();
        if (categoria == null || grupo == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione a categoria e o parâmetro/grupo.");
            return;
        }

        int qtdMinima;
        try {
            qtdMinima = Integer.parseInt(txtQtdMinimaMeta.getText().trim());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "Informe um número inteiro válido para a quantidade mínima.");
            return;
        }

        try {
            GerenciarPesquisasController.getPesquisaController().definirMetaPlanoAmostral(
                    pesquisaAtual.getCodigo(), categoria, grupo, qtdMinima
            );
            txtQtdMinimaMeta.clear();
            atualizarTabelaPlanoAmostral();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Regra de Negócio", e.getMessage());
        }
    }

    @FXML
    public void removerMetaAmostral(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;

        MetaAmostralItem selecionada = tblMetasAmostrais.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma meta na tabela para remover.");
            return;
        }

        try {
            GerenciarPesquisasController.getPesquisaController().removerMetaPlanoAmostral(
                    pesquisaAtual.getCodigo(), selecionada.categoria, selecionada.grupo
            );
            txtQtdMinimaMeta.clear();
            atualizarTabelaPlanoAmostral();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
        }
    }

    private void atualizarTabelaPlanoAmostral() {
        if (pesquisaAtual == null) return;

        int previstas = pesquisaAtual.getEntrevistasPrevistas();
        lblResumoPrevistas.setText("Entrevistas previstas na pesquisa: " + previstas);

        List<MetaAmostralItem> linhas = new ArrayList<>();
        PlanoAmostral plano = pesquisaAtual.getPlanoAmostral();
        if (plano != null) {
            for (Map.Entry<String, Map<String, Integer>> catEntry : plano.getMetasPorCategoria().entrySet()) {
                String cat = catEntry.getKey();
                int somaCat = plano.getSomaCategoria(cat);
                String resumo = somaCat + " / " + previstas;
                for (Map.Entry<String, Integer> grpEntry : catEntry.getValue().entrySet()) {
                    linhas.add(new MetaAmostralItem(cat, grpEntry.getKey(), grpEntry.getValue(), resumo));
                }
            }
        }
        tblMetasAmostrais.setItems(FXCollections.observableArrayList(linhas));
        atualizarLabelSomaCategoria();
    }

    private void atualizarLabelSomaCategoria() {
        if (pesquisaAtual == null) return;
        int previstas = pesquisaAtual.getEntrevistasPrevistas();
        String cat = cbCategoriaMeta.getValue();
        int soma = (cat != null && pesquisaAtual.getPlanoAmostral() != null)
                ? pesquisaAtual.getPlanoAmostral().getSomaCategoria(cat)
                : 0;
        lblSomaCategoria.setText("Soma da categoria" + (cat != null ? " (" + cat + ")" : "") + ": " + soma + " / " + previstas);
    }

    public static class MetaAmostralItem {
        public final String categoria;
        public final String grupo;
        public final int quantidadeMinima;
        public final String resumoCategoria;

        public MetaAmostralItem(String categoria, String grupo, int quantidadeMinima, String resumoCategoria) {
            this.categoria = categoria;
            this.grupo = grupo;
            this.quantidadeMinima = quantidadeMinima;
            this.resumoCategoria = resumoCategoria;
        }
    }

    // botoes de baixo

    @FXML
    public void salvarConfiguracao(ActionEvent event) {
        if (!verificarPesquisaSelecionada()) return;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Configuração Salva",
                "Todas as alterações da pesquisa '" + pesquisaAtual.getNome() + "' já foram salvas automaticamente!");
    }

    @FXML
    public void voltarParaGerenciar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/GerenciarPesquisa.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    private boolean verificarPesquisaSelecionada() {
        if (pesquisaAtual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma pesquisa no topo da tela primeiro.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}