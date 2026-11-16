package org.example.viewControllers.CandidatesController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.models.candidatos.IndicadoresCadastrais;
import org.example.models.comum.Cargo;
import org.example.serviceControllers.candidatos.CandidatoController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//tela de estatisticas: mostra os indicadores cadastrais numa tabela critério/quantidade
public class EstatisticasController extends TelaCandidatos {

    //uma linha da tabela: o nome do indicador e o seu valor ja formatado
    private record LinhaIndicador(String criterio, String valor) { }

    @FXML private ComboBox<String> caixaDados;
    @FXML private TableView<LinhaIndicador> tabelaIndicadores;
    @FXML private TableColumn<LinhaIndicador, String> colunaCriterio;
    @FXML private TableColumn<LinhaIndicador, String> colunaQuantidade;

    private final CandidatoController candidatoController = new CandidatoController();

    @FXML
    public void initialize() {
        colunaCriterio.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().criterio()));
        colunaQuantidade.setCellValueFactory(linha -> new SimpleStringProperty(linha.getValue().valor()));
        caixaDados.setValue("Todos"); //ao abrir, mostra todos os indicadores
        mostrarIndicadores();
    }

    //chamado ao abrir e sempre que o usuario troca a opcao da caixa
    @FXML
    public void mostrarIndicadores() {
        IndicadoresCadastrais ind = candidatoController.gerarIndicadores(); //calcula com os dados atuais
        String opcao = caixaDados.getValue();
        List<LinhaIndicador> linhas = new ArrayList<>();

        if (opcao.equals("Todos") || opcao.equals("Candidatos")) {
            linhas.add(new LinhaIndicador("Candidatos cadastrados", String.valueOf(ind.getTotalCandidatos())));
        }
        if (opcao.equals("Todos") || opcao.equals("Candidaturas")) {
            linhas.add(new LinhaIndicador("Total de candidaturas", String.valueOf(ind.getTotalCandidaturas())));
            linhas.add(new LinhaIndicador("Candidaturas ativas", String.valueOf(ind.getCandidaturasAtivas())));
            linhas.add(new LinhaIndicador("Candidaturas inativas", String.valueOf(ind.getCandidaturasInativas())));
            //todos os cargos aparecem, mesmo os que ainda nao tem candidaturas
            Map<Cargo, Integer> porCargo = ind.getCandidaturasPorCargo();
            for (Cargo cargo : Cargo.values()) {
                linhas.add(new LinhaIndicador("Candidaturas a " + cargo, String.valueOf(porCargo.getOrDefault(cargo, 0))));
            }
        }
        if (opcao.equals("Todos") || opcao.equals("Partidos")) {
            linhas.add(new LinhaIndicador("Total de partidos", String.valueOf(ind.getTotalPartidos())));
            linhas.add(new LinhaIndicador("Partidos ativos", String.valueOf(ind.getPartidosAtivos())));
            linhas.add(new LinhaIndicador("Partidos inativos", String.valueOf(ind.getPartidosInativos())));
            linhas.add(new LinhaIndicador("Média de candidaturas por partido",
                    String.format("%.2f", ind.getMediaCandidaturasPorPartido())));
            for (Map.Entry<String, Integer> entrada : ind.getCandidaturasPorPartido().entrySet()) {
                linhas.add(new LinhaIndicador("Candidaturas no " + entrada.getKey(), String.valueOf(entrada.getValue())));
            }
        }
        tabelaIndicadores.getItems().setAll(linhas);
    }
}