package org.example.viewControllers.ResearchController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PlanoAmostral;

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

    @FXML private CheckBox chkNorte;
    @FXML private CheckBox chkNordeste;
    @FXML private CheckBox chkCentroOeste;
    @FXML private CheckBox chkSudeste;
    @FXML private CheckBox chkSul;
    @FXML private CheckBox chkNacional;

    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpDataFim;
    @FXML private TextField txtEntrevistasPrevistas;
    @FXML private TextArea txtMetodologia;

    private Pesquisa pesquisaEdicao;

    @FXML
    public void initialize() {
        cbCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        cbSituacao.setValue("Planejada");
    }

    @FXML
    public void aoAlterarNacional(ActionEvent event) {
        boolean marcado = chkNacional.isSelected();
        chkNorte.setSelected(marcado);
        chkNordeste.setSelected(marcado);
        chkCentroOeste.setSelected(marcado);
        chkSudeste.setSelected(marcado);
        chkSul.setSelected(marcado);
    }

    @FXML
    public void aoAlterarRegiao(ActionEvent event) {
        boolean todasMarcadas = chkNorte.isSelected()
                && chkNordeste.isSelected()
                && chkCentroOeste.isSelected()
                && chkSudeste.isSelected()
                && chkSul.isSelected();
        chkNacional.setSelected(todasMarcadas);
    }

    private List<Regiao> obterRegioesSelecionadas() {
        List<Regiao> selecionadas = new ArrayList<>();
        if (chkNorte.isSelected()) selecionadas.add(Regiao.NORTE);
        if (chkNordeste.isSelected()) selecionadas.add(Regiao.NORDESTE);
        if (chkCentroOeste.isSelected()) selecionadas.add(Regiao.CENTRO_OESTE);
        if (chkSudeste.isSelected()) selecionadas.add(Regiao.SUDESTE);
        if (chkSul.isSelected()) selecionadas.add(Regiao.SUL);
        return selecionadas;
    }

    private String montarTextoAbrangencia(List<Regiao> regioes) {
        if (chkNacional.isSelected() || regioes.size() == 5) {
            return "Nacional";
        }
        return regioes.stream()
                .map(Regiao::getDescricao)
                .collect(Collectors.joining(", "));
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

            List<Regiao> regs = pesquisa.getRegioesAssociadas();
            boolean isNacional = "Nacional".equalsIgnoreCase(pesquisa.getAbrangencia())
                    || (regs != null && regs.contains(Regiao.NACIONAL));

            chkNorte.setSelected(isNacional || (regs != null && regs.contains(Regiao.NORTE)));
            chkNordeste.setSelected(isNacional || (regs != null && regs.contains(Regiao.NORDESTE)));
            chkCentroOeste.setSelected(isNacional || (regs != null && regs.contains(Regiao.CENTRO_OESTE)));
            chkSudeste.setSelected(isNacional || (regs != null && regs.contains(Regiao.SUDESTE)));
            chkSul.setSelected(isNacional || (regs != null && regs.contains(Regiao.SUL)));
            aoAlterarRegiao(null);
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

            List<Regiao> regioesSelecionadas = obterRegioesSelecionadas();
            if (regioesSelecionadas.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Abrangência obrigatória", "Selecione pelo menos uma região (ou Nacional).");
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
                pesquisaEdicao.setRegioesAssociadas(regioesSelecionadas);
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