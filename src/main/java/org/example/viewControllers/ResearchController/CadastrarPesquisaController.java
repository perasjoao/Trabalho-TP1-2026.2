package org.example.viewControllers.ResearchController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PlanoAmostral;
import org.example.serviceControllers.ParametroController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CadastrarPesquisaController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private ComboBox<Cargo> cbCargo;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<String> cbSituacao;
    @FXML private FlowPane fpRegioes;

    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;
    @FXML private TextField txtEntrevistasPrevistas;
    @FXML private TextArea txtMetodologia;

    private Pesquisa pesquisaEdicao;
    private CheckBox chkSelecionarTodas;
    private final List<CheckBox> checkBoxesRegioes = new ArrayList<>();
    private boolean atualizandoCheckBoxes = false;

    @FXML
    public void initialize() {
        cbCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        cbSituacao.setValue("Planejada");
        carregarCheckBoxesRegioes();
    }

    private void carregarCheckBoxesRegioes() {
        fpRegioes.getChildren().clear();
        checkBoxesRegioes.clear();

        List<String> regioesCadastradas = new ParametroController().listarRegioes();

        chkSelecionarTodas = new CheckBox("Selecionar todas");
        chkSelecionarTodas.setStyle("-fx-font-weight: bold;");
        chkSelecionarTodas.selectedProperty().addListener((obs, estava, agora) -> {
            if (atualizandoCheckBoxes) return;
            atualizandoCheckBoxes = true;
            for (CheckBox chk : checkBoxesRegioes) {
                chk.setSelected(agora);
            }
            atualizandoCheckBoxes = false;
        });

        for (String regiao : regioesCadastradas) {
            adicionarCheckBoxRegiao(regiao);
        }

        fpRegioes.getChildren().add(chkSelecionarTodas);
    }

    private CheckBox adicionarCheckBoxRegiao(String regiao) {
        CheckBox chk = new CheckBox(regiao);
        chk.selectedProperty().addListener((obs, estava, agora) -> sincronizarSelecionarTodas());
        checkBoxesRegioes.add(chk);
        if (chkSelecionarTodas != null && fpRegioes.getChildren().contains(chkSelecionarTodas)) {
            int idxTodas = fpRegioes.getChildren().indexOf(chkSelecionarTodas);
            fpRegioes.getChildren().add(idxTodas, chk);
        } else {
            fpRegioes.getChildren().add(chk);
        }
        return chk;
    }

    private void sincronizarSelecionarTodas() {
        if (atualizandoCheckBoxes || chkSelecionarTodas == null) return;
        atualizandoCheckBoxes = true;
        boolean todas = !checkBoxesRegioes.isEmpty()
                && checkBoxesRegioes.stream().allMatch(CheckBox::isSelected);
        chkSelecionarTodas.setSelected(todas);
        atualizandoCheckBoxes = false;
    }

    private List<String> obterRegioesSelecionadas() {
        return checkBoxesRegioes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
    }

    private String montarTextoAbrangencia(List<String> regioes) {
        if (!checkBoxesRegioes.isEmpty() && regioes.size() == checkBoxesRegioes.size() && regioes.size() > 1) {
            return "Todas (" + regioes.size() + " regiões)";
        }
        return String.join(", ", regioes);
    }

    public void setPesquisaEdicao(Pesquisa pesquisa) {
        this.pesquisaEdicao = pesquisa;
        if (pesquisa != null) {
            lblTitulo.setText("EDIÇÃO DE PESQUISA");
            txtCodigo.setText(pesquisa.getCodigo());
            txtCodigo.setDisable(true);
            txtNome.setText(pesquisa.getNome());
            cbCargo.setValue(pesquisa.getCargo());
            cbTipo.setValue(pesquisa.getTipo());
            cbSituacao.setValue(pesquisa.getSituacao());
            dpDataInicio.setValue(pesquisa.getDataInicio());
            dpDataFim.setValue(pesquisa.getDataFim());
            txtEntrevistasPrevistas.setText(String.valueOf(pesquisa.getEntrevistasPrevistas()));
            txtMetodologia.setText(pesquisa.getMetodologia());

            List<String> regsSalvas = pesquisa.getRegioesAssociadas();
            boolean marcarTodasLegado = "Nacional".equalsIgnoreCase(pesquisa.getAbrangencia())
                    || "Todas".equalsIgnoreCase(pesquisa.getAbrangencia());

            atualizandoCheckBoxes = true;
            for (CheckBox chk : checkBoxesRegioes) {
                boolean contem = marcarTodasLegado || (regsSalvas != null && regsSalvas.stream()
                        .anyMatch(r -> r.equalsIgnoreCase(chk.getText())));
                chk.setSelected(contem);
            }


            if (regsSalvas != null) {
                for (String regSalva : regsSalvas) {
                    boolean jaExiste = checkBoxesRegioes.stream()
                            .anyMatch(c -> c.getText().equalsIgnoreCase(regSalva));
                    if (!jaExiste && !regSalva.equalsIgnoreCase("Todas")) {
                        CheckBox extra = adicionarCheckBoxRegiao(regSalva);
                        extra.setSelected(true);
                    }
                }
            }
            atualizandoCheckBoxes = false;
            sincronizarSelecionarTodas();
        } else {
            lblTitulo.setText("CADASTRO DE PESQUISA");
            txtCodigo.setDisable(false);
            cbSituacao.setValue("Planejada");
        }
    }

    @FXML
    public void salvarPesquisa(ActionEvent event) {
        try {
            String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
            String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
            Cargo cargo = cbCargo.getValue();
            String tipo = cbTipo.getValue();
            String situacao = cbSituacao.getValue();
            LocalDate dataInicio = dpDataInicio.getValue();
            LocalDate dataFim = dpDataFim.getValue();
            String metodologia = txtMetodologia.getText() != null ? txtMetodologia.getText().trim() : "";

            List<String> regioesSelecionadas = obterRegioesSelecionadas();
            if (regioesSelecionadas.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Abrangência obrigatória", "Selecione pelo menos uma região.");
                return;
            }
            String abrangencia = montarTextoAbrangencia(regioesSelecionadas);

            int entrevistasPrevistas;
            try {
                entrevistasPrevistas = Integer.parseInt(txtEntrevistasPrevistas.getText().trim());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Dado inválido", "Informe um número inteiro válido para as entrevistas previstas.");
                return;
            }

            if (pesquisaEdicao == null) {
                Pesquisa nova = new Pesquisa(
                        codigo, nome, cargo, tipo, situacao,
                        abrangencia, dataInicio, dataFim,
                        entrevistasPrevistas, metodologia, new ArrayList<>()
                );
                nova.setRegioesAssociadas(regioesSelecionadas);
                if (!metodologia.isEmpty()) {
                    nova.setPlanoAmostral(new PlanoAmostral(metodologia));
                }
                GerenciarPesquisasController.getPesquisaController().salvar(nova);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Pesquisa cadastrada com sucesso!");
            } else {
                Pesquisa atualizada = new Pesquisa(
                        pesquisaEdicao.getCodigo(), nome, cargo, tipo, situacao,
                        abrangencia, dataInicio, dataFim,
                        entrevistasPrevistas, metodologia, pesquisaEdicao.getPerguntas()
                );
                atualizada.setRegioesAssociadas(regioesSelecionadas);
                GerenciarPesquisasController.getPesquisaController().atualizar(pesquisaEdicao.getCodigo(), atualizada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Pesquisa atualizada com sucesso!");
            }

            voltarParaGerenciar(event);

        } catch (PesquisaInvalidaException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível retornar à tela anterior: " + e.getMessage());
        }
    }

    @FXML
    public void voltarParaGerenciar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view.Research/GerenciarPesquisa.fxml"));
        Stage tela = (Stage) ((Node) event.getSource()).getScene().getWindow();
        tela.getScene().setRoot(root);
        tela.show();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}