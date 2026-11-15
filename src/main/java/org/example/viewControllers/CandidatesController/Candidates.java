package org.example.viewControllers.CandidatesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

//menus do modulo (Candidatos e Partidos e Gerenciar Partidos): so navegacao
//a troca de tela e o Voltar ao menu do modulo vem da TelaCandidatos
public class Candidates extends TelaCandidatos {

    private static final String PASTA = "/view.Candidates/";

    //menu Candidatos e Partidos
    @FXML
    public void AbrirCadastraCadidato(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "CadastrarCandidato.fxml");
    }

    @FXML
    public void AbrirCadastraPartido(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "CadastrarPartido.fxml");
    }

    @FXML
    public void AbrirGerenciarCandidato(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "GerenciarCandidato.fxml");
    }

    @FXML
    public void AbrirConsultaCandidato(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "ConsultarCandidato.fxml");
    }

    @FXML
    public void AbrirAssociaCandidato(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "AssociaCandidato.fxml");
    }

    @FXML
    public void AbrirEst(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "Estatisticas.fxml");
    }

    @FXML
    public void AbrirMenuPrincipal(ActionEvent event) throws IOException {
        trocarTela(event, "/view.Menu/Menu.fxml");
    }

    //menu Gerenciar Partidos
    @FXML
    public void AbrirAtualizarPartido(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "AtualizarPartido.fxml");
    }

    @FXML
    public void AbrirExcluirPartido(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "ExcluirPartido.fxml");
    }

    @FXML
    public void AbrirBuscarPartido(ActionEvent event) throws IOException {
        trocarTela(event, PASTA + "BuscarPartido.fxml");
    }
}