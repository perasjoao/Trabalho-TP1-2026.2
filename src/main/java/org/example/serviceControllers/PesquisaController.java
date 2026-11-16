package org.example.serviceControllers;

import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.Entrevista;
import org.example.models.candidatos.Candidatura;
import org.example.models.coleta.Participante;
import org.example.models.comum.Cargo;
import org.example.models.comum.Regiao;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PesquisaRepository;
import org.example.models.researchModels.PlanoAmostral;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PesquisaController {
    private List<Pesquisa> pesquisas;
    private final PesquisaRepository repository;

    private static final List<String> TIPOS_VALIDOS = Arrays.asList("Intenção de voto", "Conhecimento", "Rejeição", "Segundo turno simulado");
    private static final List<String> SITUACOES_VALIDAS = Arrays.asList("Planejada", "Em coleta", "Encerrada", "Processada");

    public PesquisaController() {
        this.repository = new PesquisaRepository();
        try {
            this.pesquisas = repository.carregarTodas();
        } catch (PersistenciaException e) {
            System.err.println("Aviso: " + e.getMessage());
            this.pesquisas = new ArrayList<>();
        }
    }

    // --- 1. SALVAR / CADASTRAR ---
    public void salvar(Pesquisa pesquisa) throws PesquisaInvalidaException, PersistenciaException {
        validarPesquisa(pesquisa);
        try {
            buscarPorCodigo(pesquisa.getCodigo());
            throw new PesquisaInvalidaException("Já existe uma pesquisa cadastrada com o código: " + pesquisa.getCodigo());
        } catch (PesquisaInvalidaException e) {
            if (!e.getMessage().contains("Nenhuma pesquisa encontrada")) {
                throw e;
            }
        }

        if ("Processada".equalsIgnoreCase(pesquisa.getSituacao())) {
            throw new PesquisaInvalidaException("Uma pesquisa não pode ser cadastrada diretamente como 'Processada'.");
        }
        validarRegrasDeSituacao(pesquisa, pesquisa.getSituacao());

        pesquisas.add(pesquisa);
        repository.salvarTodas(pesquisas);
    }

    // --- 2. EDITAR / ATUALIZAR ---
    public void atualizar(String codigo, Pesquisa pesquisaAtualizada) throws PesquisaInvalidaException, PersistenciaException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new PesquisaInvalidaException("Código é obrigatório para atualização.");
        }

        Pesquisa pesquisaExistente = buscarPorCodigo(codigo);

        if ("Processada".equalsIgnoreCase(pesquisaExistente.getSituacao()) &&
                "Processada".equalsIgnoreCase(pesquisaAtualizada.getSituacao())) {
            throw new PesquisaInvalidaException("Pesquisas com situação 'Processada' não podem ter seus dados alterados. Altere a situação para reprocessar.");
        }

        validarCamposEditaveis(pesquisaAtualizada);
        boolean temCandidatos = !pesquisaExistente.getCandidaturasAssociadas().isEmpty()
                || !pesquisaExistente.getNumerosCandidaturasSalvas().isEmpty();
        if (temCandidatos && pesquisaExistente.getCargo() != pesquisaAtualizada.getCargo()) {
            throw new PesquisaInvalidaException("Não é possível alterar o cargo da pesquisa enquanto houver candidatos associados. Remova-os primeiro.");
        }
        validarTransicaoSituacao(pesquisaExistente, pesquisaAtualizada.getSituacao());

        pesquisaExistente.setNome(pesquisaAtualizada.getNome());
        pesquisaExistente.setCargo(pesquisaAtualizada.getCargo());
        pesquisaExistente.setTipo(pesquisaAtualizada.getTipo());
        pesquisaExistente.setSituacao(pesquisaAtualizada.getSituacao());
        pesquisaExistente.setAbrangencia(pesquisaAtualizada.getAbrangencia());
        pesquisaExistente.setDataInicio(pesquisaAtualizada.getDataInicio());
        pesquisaExistente.setDataFim(pesquisaAtualizada.getDataFim());
        pesquisaExistente.setEntrevistasPrevistas(pesquisaAtualizada.getEntrevistasPrevistas());
        pesquisaExistente.setMetodologia(pesquisaAtualizada.getMetodologia());

        repository.salvarTodas(pesquisas);
    }

    // --- ATUALIZAR PLANO AMOSTRAL (Tela Configurar Pesquisa) ---
    public void atualizarPlanoAmostral(String codigoPesquisa, PlanoAmostral planoAmostral)
            throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        p.setPlanoAmostral(planoAmostral);
        if (planoAmostral != null) {
            p.setMetodologia(planoAmostral.getDescricaoMetodologia());
        }
        repository.salvarTodas(pesquisas);
    }

    // --- BUSCA COM FILTROS COMBINADOS ---
    public List<Pesquisa> buscarComFiltros(String termo, Cargo cargo, String tipo, String situacao,
                                           Regiao regiao, Integer qtdMinEntrevistados,
                                           LocalDate dataInicio, LocalDate dataFim,
                                           boolean apenasAbaixoDaMeta, List<Entrevista> todasEntrevistas) {
        sincronizarEntrevistasRealizadas(todasEntrevistas);

        return pesquisas.stream()
                .filter(p -> (termo == null || termo.trim().isEmpty() ||
                        p.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        p.getCodigo().equalsIgnoreCase(termo) ||
                        p.getCandidaturasAssociadas().stream().anyMatch(c ->
                                c.getCandidato().getNome().toLowerCase().contains(termo.toLowerCase()))))
                .filter(p -> (cargo == null || p.getCargo() == cargo))
                .filter(p -> (tipo == null || tipo.trim().isEmpty() || p.getTipo().equalsIgnoreCase(tipo)))
                .filter(p -> (situacao == null || situacao.trim().isEmpty() || p.getSituacao().equalsIgnoreCase(situacao)))
                .filter(p -> (regiao == null || p.getRegioesAssociadas().contains(regiao) ||
                        (p.getAbrangencia() != null && p.getAbrangencia().equalsIgnoreCase(regiao.getDescricao()))))
                .filter(p -> (qtdMinEntrevistados == null || p.getEntrevistasPrevistas() >= qtdMinEntrevistados))
                .filter(p -> (dataInicio == null || (p.getDataInicio() != null && !p.getDataInicio().isBefore(dataInicio))))
                .filter(p -> (dataFim == null || (p.getDataFim() != null && !p.getDataFim().isAfter(dataFim))))
                .filter(p -> (!apenasAbaixoDaMeta || estaAbaixoDaMetaAmostral(p, todasEntrevistas)))
                .collect(Collectors.toList());
    }

    // --- BUSCAR POR CÓDIGO ---
    public Pesquisa buscarPorCodigo(String codigo) throws PesquisaInvalidaException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new PesquisaInvalidaException("Código informado é inválido.");
        }
        for (Pesquisa p : pesquisas) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        throw new PesquisaInvalidaException("Nenhuma pesquisa encontrada com o código: " + codigo);
    }

    // --- 3. DELETAR ---
    public void deletar(String codigo) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa pesquisaParaRemover = buscarPorCodigo(codigo);
        if ("Processada".equalsIgnoreCase(pesquisaParaRemover.getSituacao())) {
            throw new PesquisaInvalidaException("Não é possível excluir uma pesquisa que já foi 'Processada'.");
        }
        pesquisas.remove(pesquisaParaRemover);
        repository.salvarTodas(pesquisas);
    }

    public void deletarPesquisa(String codigo) throws PesquisaInvalidaException, PersistenciaException {
        deletar(codigo);
    }

    // --- 4. VERIFICAÇÃO PARA O MÓDULO DE COLETA ---
    public boolean podeReceberEntrevista(String codigoPesquisa) throws PesquisaInvalidaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        return !"Encerrada".equalsIgnoreCase(p.getSituacao()) &&
                !"Processada".equalsIgnoreCase(p.getSituacao());
    }

    // --- 5. ASSOCIAÇÃO DE REGIÕES ---
    public void associarRegiao(String codigoPesquisa, Regiao regiao) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (regiao == null) {
            throw new PesquisaInvalidaException("Selecione uma região válida.");
        }
        if (p.getRegioesAssociadas().contains(regiao)) {
            throw new PesquisaInvalidaException("A região " + regiao + " já está associada a esta pesquisa.");
        }
        p.getRegioesAssociadas().add(regiao);
        repository.salvarTodas(pesquisas);
    }

    public void removerRegiao(String codigoPesquisa, Regiao regiao) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        p.getRegioesAssociadas().remove(regiao);
        repository.salvarTodas(pesquisas);
    }

    // --- 6. ASSOCIAÇÃO DE CANDIDATURAS ---
    public void associarCandidatura(String codigoPesquisa, Candidatura candidatura) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (candidatura == null) {
            throw new PesquisaInvalidaException("Selecione uma candidatura válida.");
        }
        if (candidatura.getCargo() != p.getCargo()) {
            throw new PesquisaInvalidaException("Cargo incompatível: a pesquisa é para " + p.getCargo() +
                    ", mas a candidatura é para " + candidatura.getCargo() + ".");
        }
        if (!candidatura.podeParticiparDePesquisa()) {
            throw new PesquisaInvalidaException("A candidatura de " + candidatura.getCandidato().getNome() +
                    " está com situação '" + candidatura.getSituacao() + "' e não pode participar de pesquisas.");
        }
        boolean jaAssociada = p.getCandidaturasAssociadas().stream()
                .anyMatch(c -> c.getNumero() == candidatura.getNumero());
        if (jaAssociada || p.getNumerosCandidaturasSalvas().contains(candidatura.getNumero())) {
            throw new PesquisaInvalidaException("Esta candidatura já está associada a esta pesquisa.");
        }

        p.getCandidaturasAssociadas().add(candidatura);
        if (!p.getNumerosCandidaturasSalvas().contains(candidatura.getNumero())) {
            p.getNumerosCandidaturasSalvas().add(candidatura.getNumero());
        }
        repository.salvarTodas(pesquisas);
    }

    public void removerCandidatura(String codigoPesquisa, Candidatura candidatura) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (candidatura != null) {
            p.getCandidaturasAssociadas().removeIf(c -> c.getNumero() == candidatura.getNumero());
            p.getNumerosCandidaturasSalvas().remove(Integer.valueOf(candidatura.getNumero()));
            repository.salvarTodas(pesquisas);
        }
    }

    public void vincularCandidaturasCarregadas(List<Candidatura> todasCandidaturasDoSistema) {
        if (todasCandidaturasDoSistema == null) return;
        for (Pesquisa p : pesquisas) {
            if (p.getCandidaturasAssociadas().isEmpty() && !p.getNumerosCandidaturasSalvas().isEmpty()) {
                for (Integer numero : p.getNumerosCandidaturasSalvas()) {
                    todasCandidaturasDoSistema.stream()
                            .filter(c -> c.getNumero() == numero)
                            .findFirst()
                            .ifPresent(c -> p.getCandidaturasAssociadas().add(c));
                }
            }
        }
    }

    // --- 7. GESTÃO DE PERGUNTAS E OPÇÕES DE RESPOSTA ---
    public void adicionarPergunta(String codigoPesquisa, Pergunta pergunta) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (pergunta == null || pergunta.getEnunciado() == null || pergunta.getEnunciado().trim().isEmpty()) {
            throw new PesquisaInvalidaException("O enunciado da pergunta é obrigatório.");
        }
        if (pergunta.getTipo() == null || pergunta.getTipo().trim().isEmpty()) {
            throw new PesquisaInvalidaException("O tipo da pergunta é obrigatório.");
        }
        boolean idDuplicado = p.getPerguntas().stream()
                .anyMatch(q -> q.getId().equalsIgnoreCase(pergunta.getId()));
        if (idDuplicado) {
            throw new PesquisaInvalidaException("Já existe uma pergunta com o código/ID: " + pergunta.getId());
        }

        p.getPerguntas().add(pergunta);
        repository.salvarTodas(pesquisas);
    }

    public void editarPergunta(String codigoPesquisa, String idPergunta, String novoEnunciado, String novoTipo, List<OpcaoResposta> novasOpcoes)
            throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        Pergunta alvo = p.getPerguntas().stream()
                .filter(q -> q.getId().equalsIgnoreCase(idPergunta))
                .findFirst()
                .orElseThrow(() -> new PesquisaInvalidaException("Pergunta não encontrada."));

        if (novoEnunciado == null || novoEnunciado.trim().isEmpty()) {
            throw new PesquisaInvalidaException("O enunciado da pergunta não pode ser vazio.");
        }
        if (novoTipo == null || novoTipo.trim().isEmpty()) {
            throw new PesquisaInvalidaException("O tipo da pergunta é obrigatório.");
        }

        alvo.setEnunciado(novoEnunciado);
        alvo.setTipo(novoTipo);
        if (novasOpcoes != null) {
            alvo.setOpcoes(novasOpcoes);
        }
        repository.salvarTodas(pesquisas);
    }

    public void removerPergunta(String codigoPesquisa, String idPergunta) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        boolean removida = p.getPerguntas().removeIf(q -> q.getId().equalsIgnoreCase(idPergunta));
        if (!removida) {
            throw new PesquisaInvalidaException("Pergunta não encontrada para remoção.");
        }
        repository.salvarTodas(pesquisas);
    }

    // --- INTEGRAÇÃO COM COLETA E META AMOSTRAL ---
    public void sincronizarEntrevistasRealizadas(List<Entrevista> todasEntrevistas) {
        if (todasEntrevistas == null) return;
        for (Pesquisa p : pesquisas) {
            int count = (int) todasEntrevistas.stream()
                    .filter(e -> p.getCodigo().equalsIgnoreCase(e.getCodigoPesquisa()))
                    .count();
            p.setEntrevistasRealizadas(count);
        }
    }

    public boolean estaAbaixoDaMetaAmostral(Pesquisa pesquisa, List<Entrevista> todasEntrevistas) {
        if (pesquisa == null || pesquisa.getPlanoAmostral() == null ||
                pesquisa.getPlanoAmostral().getMetasPorGrupo().isEmpty()) {
            return false;
        }

        Map<String, Integer> contagemPorGrupo = new HashMap<>();
        if (todasEntrevistas != null) {
            for (Entrevista e : todasEntrevistas) {
                if (pesquisa.getCodigo().equalsIgnoreCase(e.getCodigoPesquisa()) && e.getParticipante() != null) {
                    Participante part = e.getParticipante();
                    incrementarGrupo(contagemPorGrupo, part.getRegiao());
                    incrementarGrupo(contagemPorGrupo, part.getFaixaEtaria());
                    incrementarGrupo(contagemPorGrupo, part.getGenero());
                    incrementarGrupo(contagemPorGrupo, part.getEscolaridade());
                    incrementarGrupo(contagemPorGrupo, part.getFaixaRenda());
                }
            }
        }
        return !pesquisa.getPlanoAmostral().atingiuMetaTodosGrupos(contagemPorGrupo);
    }

    private void incrementarGrupo(Map<String, Integer> mapa, String chave) {
        if (chave != null && !chave.trim().isEmpty()) {
            mapa.put(chave, mapa.getOrDefault(chave, 0) + 1);
        }
    }

    // --- LISTAR TODAS ---
    public List<Pesquisa> listarTodas() throws PesquisaInvalidaException {
        if (pesquisas.isEmpty()) {
            throw new PesquisaInvalidaException("Nenhuma pesquisa cadastrada no sistema.");
        }
        return new ArrayList<>(pesquisas);
    }

    // Versão segura para TableView (retorna lista vazia sem lançar exceção)
    public List<Pesquisa> getPesquisas() {
        return new ArrayList<>(pesquisas);
    }

    // --- VALIDAÇÕES DE REGRAS DE NEGÓCIO ---
    private void validarPesquisa(Pesquisa pesquisa) throws PesquisaInvalidaException {
        if (pesquisa == null) {
            throw new PesquisaInvalidaException("A pesquisa não pode ser nula.");
        }
        if (pesquisa.getCodigo() == null || pesquisa.getCodigo().trim().isEmpty()) {
            throw new PesquisaInvalidaException("O código da pesquisa é obrigatório.");
        }
        validarCamposEditaveis(pesquisa);
    }

    private void verificarBloqueioProcessada(Pesquisa p) throws PesquisaInvalidaException {
        if ("Processada".equalsIgnoreCase(p.getSituacao())) {
            throw new PesquisaInvalidaException("Esta pesquisa está 'Processada' e seus dados não podem ser modificados.");
        }
    }

    private void validarTransicaoSituacao(Pesquisa atual, String novaSituacao) throws PesquisaInvalidaException {
        if ("Processada".equalsIgnoreCase(novaSituacao) && !"Encerrada".equalsIgnoreCase(atual.getSituacao())) {
            throw new PesquisaInvalidaException("Uma pesquisa somente pode ser 'Processada' após estar 'Encerrada'.");
        }
        validarRegrasDeSituacao(atual, novaSituacao);
    }

    private void validarRegrasDeSituacao(Pesquisa p, String situacaoAlvo) throws PesquisaInvalidaException {
        if (!"Planejada".equalsIgnoreCase(situacaoAlvo) && "Intenção de voto".equalsIgnoreCase(p.getTipo())) {
            boolean temCandidato = !p.getCandidaturasAssociadas().isEmpty() || !p.getNumerosCandidaturasSalvas().isEmpty();
            if (!temCandidato) {
                throw new PesquisaInvalidaException("Pesquisas do tipo 'Intenção de voto' devem possuir pelo menos um candidato associado antes de iniciar a coleta.");
            }
        }
    }

    private void validarCamposEditaveis(Pesquisa pesquisa) throws PesquisaInvalidaException {
        if (pesquisa.getNome() == null || pesquisa.getNome().trim().isEmpty()) {
            throw new PesquisaInvalidaException("O nome da pesquisa é obrigatório.");
        }
        if (pesquisa.getCargo() == null) {
            throw new PesquisaInvalidaException("O cargo da pesquisa é obrigatório.");
        }
        if (pesquisa.getTipo() == null || !contemIgnorandoCase(TIPOS_VALIDOS, pesquisa.getTipo())) {
            throw new PesquisaInvalidaException("Tipo de pesquisa inválido. Opções aceitas: " + TIPOS_VALIDOS);
        }
        if (pesquisa.getSituacao() == null || !contemIgnorandoCase(SITUACOES_VALIDAS, pesquisa.getSituacao())) {
            throw new PesquisaInvalidaException("Situação inválida. Opções aceitas: " + SITUACOES_VALIDAS);
        }
        if (pesquisa.getEntrevistasPrevistas() <= 0) {
            throw new PesquisaInvalidaException("A quantidade de entrevistas previstas deve ser maior que zero.");
        }
        if (pesquisa.getDataInicio() != null && pesquisa.getDataFim() != null && pesquisa.getDataFim().isBefore(pesquisa.getDataInicio())) {
            throw new PesquisaInvalidaException("A data final do período de coleta não pode ser anterior à data inicial.");
        }
    }

    private boolean contemIgnorandoCase(List<String> lista, String valor) {
        return lista.stream().anyMatch(item -> item.equalsIgnoreCase(valor));
    }
}