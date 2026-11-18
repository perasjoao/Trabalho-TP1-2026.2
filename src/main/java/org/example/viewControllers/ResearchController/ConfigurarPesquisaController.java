package org.example.viewControllers.ResearchController;

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
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.candidatos.Candidatura;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.serviceControllers.candidatos.CandidatoController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurarPesquisaController {

    private final CandidatoController candidatoController = new CandidatoController();

    @FXML private ComboBox<Pesquisa> cbPesquisa;

    // Aba 1: Perguntas
    @FXML private TextField txtEnunciadoPergunta;
    @FXML private ComboBox<String> cbTipoPergunta;
    @FXML private TextField txtNovaOpcao;
    @FXML private ListView<OpcaoResposta> listOpcoes;
    @FXML private TableView<Pergunta> tblPerguntas;
    @FXML private TableColumn<Pergunta, String> colNumPergunta;
    @FXML private TableColumn<Pergunta, String> colTextoPergunta;
    @FXML private TableColumn<Pergunta, String> colTipoPergunta;
    @FXML private TableColumn<Pergunta, String> colOpcoesPergunta;

    // Aba 2: Candidatos
    @FXML private ListView<Candidatura> listCandidatosDisponiveis;
    @FXML private ListView<Candidatura> listCandidatosAssociados;

    private Pesquisa pesquisaAtual;
    private final ObservableList<OpcaoResposta> opcoesEmEdicao = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbPesquisa.setItems(FXCollections.observableArrayList(
                GerenciarPesquisasController.getPesquisaController().getPesquisas()
        ));

        cbTipoPergunta.setItems(FXCollections.observableArrayList(
                "Única Escolha", "Múltipla Escolha", "Escala", "Resposta Livre"
        ));

        listOpcoes.setItems(opcoesEmEdicao);

        // Formata a exibição das candidaturas nas duas ListViews usando getDescricao()
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

        tblPerguntas.getSelectionModel().selectedItemProperty().addListener((obs, antiga, selecionada) -> {
            if (selecionada != null) {
                txtEnunciadoPergunta.setText(selecionada.getEnunciado());
                cbTipoPergunta.setValue(selecionada.getTipo());
                opcoesEmEdicao.setAll(selecionada.getOpcoes() != null ? selecionada.getOpcoes() : new ArrayList<>());
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

        // 1. Atualiza tabela de perguntas
        tblPerguntas.setItems(FXCollections.observableArrayList(pesquisaAtual.getPerguntas()));
        limparFormularioPergunta();

        // 2. Busca as candidaturas reais do sistema e vincula as já salvas no TXT
        List<Candidatura> todasCandidaturas = candidatoController.listar();
        GerenciarPesquisasController.getPesquisaController().vincularCandidaturasCarregadas(todasCandidaturas);

        List<Candidatura> candAssociadas = pesquisaAtual.getCandidaturasAssociadas();
        List<Candidatura> candDisponiveis = todasCandidaturas.stream()
                .filter(c -> c.getCargo() == pesquisaAtual.getCargo())
                .filter(c -> candAssociadas.stream().noneMatch(a -> a.getNumero() == c.getNumero()))
                .collect(Collectors.toList());

        listCandidatosDisponiveis.setItems(FXCollections.observableArrayList(candDisponiveis));
        listCandidatosAssociados.setItems(FXCollections.observableArrayList(candAssociadas));
    }

    // ==================== ABA 1: PERGUNTAS ====================

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
                    new ArrayList<>(opcoesEmEdicao)
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
        try {
            GerenciarPesquisasController.getPesquisaController().removerPergunta(pesquisaAtual.getCodigo(), selecionada.getId());
            atualizarTodasAsAbas();
        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
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
    }

    // ==================== ABA 2: CANDIDATOS ====================

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

    // ==================== BOTÕES INFERIORES ====================

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