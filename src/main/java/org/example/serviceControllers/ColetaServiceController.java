package org.example.serviceControllers;

import org.example.exceptions.AmostraInvalidaException;
import org.example.exceptions.ColetaIncompletaException;
import org.example.models.Entrevista;
import org.example.models.coleta.Entrevistador;
import org.example.models.coleta.Participante;
import org.example.models.researchModels.Pesquisa;
import org.example.utils.GerenciadorArquivo;
import org.example.viewControllers.ResearchController.ConsultasPesquisasController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ColetaServiceController {

    private static final String CAMINHO_ARQUIVO = "entrevistas.dat";

    //as telas da coleta usam sempre a mesma instancia, assim a amostra configurada
    //numa tela continua disponivel na proxima
    private static ColetaServiceController instancia;

    private final GerenciadorArquivo<Entrevista> gerenciadorArquivo = new GerenciadorArquivo<>();

    private List<Entrevista> entrevistas;
    private Entrevistador entrevistadorAtual;
    private Participante participanteAtual;

    public ColetaServiceController() {
        this.entrevistas = gerenciadorArquivo.ler(CAMINHO_ARQUIVO);
        if (this.entrevistas == null) {
            this.entrevistas = new ArrayList<>();
        }
    }

    public static ColetaServiceController getInstancia() {
        if (instancia == null) {//primeiro cria se nao tiver, depois devolve a mesma pra geral
            instancia = new ColetaServiceController();
        }
        return instancia;
    }


    public void salvarConfiguracaoAmostra(Entrevistador entrevistador, Participante participante) {
        this.entrevistadorAtual = entrevistador;
        this.participanteAtual = participante;
    }

    public boolean temAmostraConfigurada() {
        return entrevistadorAtual != null && participanteAtual != null;
    }

    public List<Entrevista> listarEntrevistasPorPesquisa(String codigoPesquisa) {
        List<Entrevista> filtradas = new ArrayList<>();
        if (codigoPesquisa == null || codigoPesquisa.trim().isEmpty()) {
            return filtradas;
        }
        String codAlvo = codigoPesquisa.trim();
        for (Entrevista e : entrevistas) {
            if (e != null && e.getCodigoPesquisa() != null) {
                String codEntrevista = e.getCodigoPesquisa().trim();
                if (codEntrevista.equalsIgnoreCase(codAlvo)
                        || codEntrevista.toLowerCase().startsWith(codAlvo.toLowerCase() + " - ")) {
                    filtradas.add(e);
                }
            }
        }
        return filtradas;
    }

    public Entrevistador getEntrevistadorAtual() {
        return entrevistadorAtual;
    }

    public Participante getParticipanteAtual() {
        return participanteAtual;
    }



    //aplica as regras da coleta antes de gravar no arquivo
    public void registrarEntrevista(Entrevista entrevista, Pesquisa pesquisa)//faz todas as verificacoes... pesquisas que ja foram encerradas,codigo de entrevista igual,quantidade prevista etc
            throws ColetaIncompletaException, AmostraInvalidaException {
        if (pesquisa == null) {
            throw new ColetaIncompletaException("Selecione a pesquisa da entrevista.");
        }
        String situacao = pesquisa.getSituacao();
        if (!"Em coleta".equalsIgnoreCase(situacao)) {
            throw new ColetaIncompletaException("A pesquisa " + pesquisa.getCodigo() + " está com situação '" + situacao
                    + "'. Apenas pesquisas 'Em coleta' podem receber novas entrevistas.");
        }

        String codigoPesquisa = pesquisa.getCodigo();
        if (existeEntrevista(codigoPesquisa, entrevista.getIdEntrevista())) {
            throw new ColetaIncompletaException("Já existe a entrevista " + entrevista.getIdEntrevista()
                    + " na pesquisa " + codigoPesquisa + ".");
        }

        String codigoParticipante = entrevista.getParticipante().getCodigo();
        boolean participanteRepetido = entrevistasDaPesquisa(codigoPesquisa).stream()
                .anyMatch(entrevistaFiltro -> entrevistaFiltro.getParticipante() != null
                        && codigoParticipante.equalsIgnoreCase(entrevistaFiltro.getParticipante().getCodigo()));
        if (participanteRepetido) {
            throw new AmostraInvalidaException("O participante " + codigoParticipante
                    + " já foi entrevistado nesta pesquisa. Use outro código de participante.");
        }

        int previstas = pesquisa.getEntrevistasPrevistas();
        if (previstas > 0 && entrevistasDaPesquisa(codigoPesquisa).size() >= previstas) {
            throw new ColetaIncompletaException("A pesquisa já atingiu as " + previstas
                    + " entrevistas previstas. Peça para aumentar a quantidade prevista no módulo de Pesquisas.");
        }

        entrevistas.add(entrevista);
        gerenciadorArquivo.salvar(CAMINHO_ARQUIVO, entrevistas);
        //atualiza na hora o realizado/percentual da tela de consulta de pesquisas
        ConsultasPesquisasController.setEntrevistasDoSistema(entrevistas);

        //cada participante responde uma entrevista so; o entrevistador continua o mesmo
        participanteAtual = null;
    }

    public boolean existeEntrevista(String codigoPesquisa, String idEntrevista) {
        return entrevistasDaPesquisa(codigoPesquisa).stream()
                .anyMatch(entrevistaFiltro -> idEntrevista.equalsIgnoreCase(entrevistaFiltro.getIdEntrevista()));
    }

    //sugere E001, E002... pulando os codigos que ja existem na pesquisa
    public String proximoCodigoEntrevista(String codigoPesquisa) {//nao pode repetir, e testa no while se ja existe, pra pular os repetidos
        int numero = entrevistasDaPesquisa(codigoPesquisa).size() + 1;
        String codigo = String.format("E%03d", numero);
        while (existeEntrevista(codigoPesquisa, codigo)) {
            numero++;
            codigo = String.format("E%03d", numero);
        }
        return codigo;
    }

    public List<Entrevista> entrevistasDaPesquisa(String codigoPesquisa) {
        return entrevistas.stream()
                .filter(entrevistaFiltro -> codigoPesquisa != null && codigoPesquisa.equalsIgnoreCase(entrevistaFiltro.getCodigoPesquisa()))
                .toList();
    }

    public List<Entrevista> filtrar(Predicate<Entrevista> filtro) {
        return entrevistas.stream().filter(filtro).toList();
    }

    public List<Entrevista> listarEntrevistas() {
        return entrevistas;
    }
}
