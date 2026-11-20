package org.example.serviceControllers;

import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.Entrevista;
import org.example.models.candidatos.Candidatura;
import org.example.models.coleta.Participante;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.OpcaoResposta;
import org.example.models.researchModels.Pergunta;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PesquisaRepository;
import org.example.models.researchModels.PlanoAmostral;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PesquisaController {
    private List<Pesquisa> pesquisas;
    private final PesquisaRepository repository;

    private static final List<String> TIPOS_VALIDOS = Arrays.asList(
            "Intenção de voto", "Conhecimento", "Rejeição", "Segundo turno simulado"
    );
    private static final List<String> SITUACOES_VALIDAS = Arrays.asList(
            "Planejada", "Em coleta", "Encerrada", "Processada"
    );

    public PesquisaController() {
        this.repository = new PesquisaRepository();
        try {
            this.pesquisas = repository.carregarTodas();
        } catch (PersistenciaException e) {
            System.err.println("Aviso: " + e.getMessage());
            this.pesquisas = new ArrayList<>();
        }
    }

    //ssalvar/cadastrar
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

    // atualiza
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
        if (pesquisaExistente.getPlanoAmostral() != null
                && pesquisaExistente.getPlanoAmostral().getMetasPorCategoria().containsKey("Região")
                && pesquisaAtualizada.getRegioesAssociadas() != null
                && !pesquisaAtualizada.getRegioesAssociadas().isEmpty()) {
            Map<String, Integer> metasRegiao = pesquisaExistente.getPlanoAmostral().getMetasPorCategoria().get("Região");
            for (String regiaoComMeta : metasRegiao.keySet()) {
                boolean aindaAssociada = pesquisaAtualizada.getRegioesAssociadas().stream()
                        .anyMatch(r -> r.equalsIgnoreCase(regiaoComMeta));
                if (!aindaAssociada) {
                    throw new PesquisaInvalidaException(
                            "Não é possível remover a região '" + regiaoComMeta +
                                    "' enquanto houver uma meta amostral definida para ela. Remova a meta primeiro."
                    );
                }
            }
        }

        if (pesquisaAtualizada.getEntrevistasPrevistas() < pesquisaExistente.getEntrevistasRealizadas()) {
            throw new PesquisaInvalidaException("A quantidade prevista não pode ser menor que o total de entrevistas já realizadas ("
                    + pesquisaExistente.getEntrevistasRealizadas() + ").");
        }

        if (pesquisaExistente.getPlanoAmostral() != null) {
            pesquisaExistente.getPlanoAmostral().validarSomasPorCategoria(pesquisaAtualizada.getEntrevistasPrevistas());
        }

        validarTransicaoSituacao(pesquisaExistente, pesquisaAtualizada.getSituacao());

        pesquisaExistente.setNome(pesquisaAtualizada.getNome());
        pesquisaExistente.setCargo(pesquisaAtualizada.getCargo());
        pesquisaExistente.setTipo(pesquisaAtualizada.getTipo());
        pesquisaExistente.setSituacao(pesquisaAtualizada.getSituacao());
        pesquisaExistente.setAbrangencia(pesquisaAtualizada.getAbrangencia());
        if (pesquisaAtualizada.getRegioesAssociadas() != null && !pesquisaAtualizada.getRegioesAssociadas().isEmpty()) {
            pesquisaExistente.setRegioesAssociadas(new ArrayList<>(pesquisaAtualizada.getRegioesAssociadas()));
        }
        pesquisaExistente.setDataInicio(pesquisaAtualizada.getDataInicio());
        pesquisaExistente.setDataFim(pesquisaAtualizada.getDataFim());
        pesquisaExistente.setEntrevistasPrevistas(pesquisaAtualizada.getEntrevistasPrevistas());
        pesquisaExistente.setMetodologia(pesquisaAtualizada.getMetodologia());

        repository.salvarTodas(pesquisas);
    }

    // plano amostral
    public void atualizarPlanoAmostral(String codigoPesquisa, PlanoAmostral planoAmostral)
            throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (planoAmostral != null) {
            planoAmostral.validarSomasPorCategoria(p.getEntrevistasPrevistas());
            p.setMetodologia(planoAmostral.getDescricaoMetodologia());
        }
        p.setPlanoAmostral(planoAmostral);
        repository.salvarTodas(pesquisas);
    }

    public void definirMetaPlanoAmostral(String codigoPesquisa, String categoria, String grupo, int quantidadeMinima)
            throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (quantidadeMinima < 0) {
            throw new PesquisaInvalidaException("A quantidade mínima da meta não pode ser negativa.");
        }
        if ("Região".equalsIgnoreCase(categoria)) {
            boolean regiaoAssociada = p.getRegioesAssociadas() != null && p.getRegioesAssociadas().stream()
                    .anyMatch(r -> r.equalsIgnoreCase(grupo.trim()));
            if (!regiaoAssociada) {
                throw new PesquisaInvalidaException(
                        "A região '" + grupo + "' não está associada à abrangência desta pesquisa."
                );
            }
        }
        PlanoAmostral plano = p.getPlanoAmostral();
        if (plano == null) {
            plano = new PlanoAmostral(p.getMetodologia());
            p.setPlanoAmostral(plano);
        }

        Integer valorAnterior = plano.getMetasPorCategoria().containsKey(categoria)
                ? plano.getMetasPorCategoria().get(categoria).get(grupo)
                : null;

        plano.definirMeta(categoria, grupo, quantidadeMinima);
        try {
            plano.validarSomasPorCategoria(p.getEntrevistasPrevistas());
        } catch (PesquisaInvalidaException e) {
            if (valorAnterior != null) {
                plano.definirMeta(categoria, grupo, valorAnterior);
            } else {
                plano.removerMeta(categoria, grupo);
            }
            throw e;
        }

        repository.salvarTodas(pesquisas);
    }

    public void removerMetaPlanoAmostral(String codigoPesquisa, String categoria, String grupo)
            throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (p.getPlanoAmostral() != null) {
            p.getPlanoAmostral().removerMeta(categoria, grupo);
            repository.salvarTodas(pesquisas);
        }
    }

    // busca
    public List<Pesquisa> buscarComFiltros(String termo, Cargo cargo, String tipo, String situacao,
                                           String regiao, Integer qtdMinEntrevistados,
                                           LocalDate dataInicio, LocalDate dataFim,
                                           boolean apenasAbaixoDaMeta, List<Entrevista> todasEntrevistas) {
        return buscarComFiltrosMultiplos(
                termo,
                cargo != null ? Collections.singletonList(cargo) : Collections.emptyList(),
                (tipo != null && !tipo.trim().isEmpty()) ? Collections.singletonList(tipo) : Collections.emptyList(),
                (situacao != null && !situacao.trim().isEmpty()) ? Collections.singletonList(situacao) : Collections.emptyList(),
                (regiao != null && !regiao.trim().isEmpty()) ? Collections.singletonList(regiao) : Collections.emptyList(),
                qtdMinEntrevistados, dataInicio, dataFim, apenasAbaixoDaMeta, todasEntrevistas
        );
    }

    public List<Pesquisa> buscarComFiltrosMultiplos(String termo, List<Cargo> cargos, List<String> tipos,
                                                    List<String> situacoes, List<String> regioes,
                                                    Integer qtdMinEntrevistados, LocalDate dataInicio, LocalDate dataFim,
                                                    boolean apenasAbaixoDaMeta, List<Entrevista> todasEntrevistas) {
        sincronizarEntrevistasRealizadas(todasEntrevistas);

        return pesquisas.stream()
                .filter(p -> (termo == null || termo.trim().isEmpty() ||
                        p.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        p.getCodigo().equalsIgnoreCase(termo) ||
                        p.getCandidaturasAssociadas().stream().anyMatch(c ->
                                c.getCandidato().getNome().toLowerCase().contains(termo.toLowerCase()))))
                .filter(p -> (cargos == null || cargos.isEmpty() || cargos.contains(p.getCargo())))
                .filter(p -> (tipos == null || tipos.isEmpty() ||
                        tipos.stream().anyMatch(t -> t.equalsIgnoreCase(p.getTipo()))))
                .filter(p -> (situacoes == null || situacoes.isEmpty() ||
                        situacoes.stream().anyMatch(s -> s.equalsIgnoreCase(p.getSituacao()))))
                .filter(p -> correspondeAlgumaRegiao(p, regioes))
                .filter(p -> (qtdMinEntrevistados == null || p.getEntrevistasPrevistas() >= qtdMinEntrevistados))
                .filter(p -> (dataInicio == null || (p.getDataInicio() != null && !p.getDataInicio().isBefore(dataInicio))))
                .filter(p -> (dataFim == null || (p.getDataFim() != null && !p.getDataFim().isAfter(dataFim))))
                .filter(p -> (!apenasAbaixoDaMeta || estaAbaixoDaMetaAmostral(p, todasEntrevistas)))
                .collect(Collectors.toList());
    }

    private boolean correspondeAlgumaRegiao(Pesquisa p, List<String> regioesFiltro) {
        if (regioesFiltro == null || regioesFiltro.isEmpty()) {
            return true;
        }
        for (String r : regioesFiltro) {
            if (r == null || r.trim().isEmpty()) continue;
            String regBusca = r.trim();

            boolean bateNaLista = p.getRegioesAssociadas() != null && p.getRegioesAssociadas().stream()
                    .anyMatch(assoc -> assoc != null && assoc.equalsIgnoreCase(regBusca));

            boolean bateNaAbrangencia = p.getAbrangencia() != null && (
                    "Nacional".equalsIgnoreCase(p.getAbrangencia().trim()) ||
                            "Todas".equalsIgnoreCase(p.getAbrangencia().trim()) ||
                            Arrays.stream(p.getAbrangencia().split(","))
                                    .map(String::trim)
                                    .anyMatch(item -> item.equalsIgnoreCase(regBusca))
            );

            if (bateNaLista || bateNaAbrangencia) {
                return true;
            }
        }
        return false;
    }

    //busca codigo
    public Pesquisa buscarPorCodigo(String codigo) throws PesquisaInvalidaException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new PesquisaInvalidaException("Código informado é inválido.");
        }
        String codLimpo = codigo.trim();
        for (Pesquisa p : pesquisas) {
            if (p.getCodigo().equalsIgnoreCase(codLimpo)
                    || codLimpo.toLowerCase().startsWith(p.getCodigo().toLowerCase() + " - ")) {
                return p;
            }
        }
        throw new PesquisaInvalidaException("Nenhuma pesquisa encontrada com o código: " + codigo);
    }

    //deletar
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

    // verifica se pode receber uma entrevista
    public boolean podeReceberEntrevista(String codigoPesquisa) throws PesquisaInvalidaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        if (!"Em coleta".equalsIgnoreCase(p.getSituacao())) {
            return false;
        }
        return p.getEntrevistasRealizadas() < p.getEntrevistasPrevistas();
    }

    // associa as regiões
    public void associarRegiao(String codigoPesquisa, String regiao) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (regiao == null || regiao.trim().isEmpty()) {
            throw new PesquisaInvalidaException("Selecione uma região válida.");
        }
        String regLimpa = regiao.trim();
        boolean jaExiste = p.getRegioesAssociadas().stream()
                .anyMatch(r -> r.equalsIgnoreCase(regLimpa));
        if (jaExiste) {
            throw new PesquisaInvalidaException("A região '" + regLimpa + "' já está associada a esta pesquisa.");
        }
        p.getRegioesAssociadas().add(regLimpa);
        repository.salvarTodas(pesquisas);
    }

    public void removerRegiao(String codigoPesquisa, String regiao) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        verificarBloqueioProcessada(p);

        if (regiao != null) {
            p.getRegioesAssociadas().removeIf(r -> r.equalsIgnoreCase(regiao.trim()));
            repository.salvarTodas(pesquisas);
        }
    }

    // associa os candidatos
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

            for (Pergunta q : p.getPerguntas()) {
                q.getCandidaturasAssociadas().removeIf(c -> c.getNumero() == candidatura.getNumero());
                q.getNumerosCandidaturasSalvas().remove(Integer.valueOf(candidatura.getNumero()));
            }
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
            for (Pergunta q : p.getPerguntas()) {
                if (q.getCandidaturasAssociadas().isEmpty() && !q.getNumerosCandidaturasSalvas().isEmpty()) {
                    List<Candidatura> vinculadas = new ArrayList<>();
                    for (Integer numero : q.getNumerosCandidaturasSalvas()) {
                        todasCandidaturasDoSistema.stream()
                                .filter(c -> c.getNumero() == numero)
                                .findFirst()
                                .ifPresent(vinculadas::add);
                    }
                    q.setCandidaturasAssociadas(vinculadas);
                }
            }
        }
    }

    // crud perguntas
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
        editarPergunta(codigoPesquisa, idPergunta, novoEnunciado, novoTipo, novasOpcoes, false, null);
    }

    public void editarPergunta(String codigoPesquisa, String idPergunta, String novoEnunciado, String novoTipo,
                               List<OpcaoResposta> novasOpcoes, boolean paraTodosCandidatos,
                               List<Candidatura> candidaturasAssociadas)
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
        alvo.setParaTodosCandidatos(paraTodosCandidatos);
        if (candidaturasAssociadas != null) {
            alvo.setCandidaturasAssociadas(candidaturasAssociadas);
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

    public List<Pergunta> buscarPerguntasPorCandidato(String codigoPesquisa, Candidatura candidato) throws PesquisaInvalidaException {
        if (candidato == null) {
            return Collections.emptyList();
        }
        return buscarPerguntasPorCandidatos(codigoPesquisa, Collections.singletonList(candidato));
    }

    public List<Pergunta> buscarPerguntasPorCandidatos(String codigoPesquisa, List<Candidatura> candidatos) throws PesquisaInvalidaException {
        Pesquisa p = buscarPorCodigo(codigoPesquisa);
        if (candidatos == null || candidatos.isEmpty()) {
            return new ArrayList<>(p.getPerguntas());
        }
        return p.getPerguntas().stream()
                .filter(q -> q.isParaTodosCandidatos() || candidatos.stream().anyMatch(q::refereSeAoCandidato))
                .collect(Collectors.toList());
    }

    // integração
    public void sincronizarEntrevistasRealizadas(List<Entrevista> todasEntrevistas) {
        if (todasEntrevistas == null) return;
        for (Pesquisa p : pesquisas) {
            String codPesquisa = p.getCodigo().trim();
            int count = (int) todasEntrevistas.stream()
                    .filter(e -> e != null && e.getCodigoPesquisa() != null && (
                            codPesquisa.equalsIgnoreCase(e.getCodigoPesquisa().trim()) ||
                                    e.getCodigoPesquisa().trim().toLowerCase().startsWith(codPesquisa.toLowerCase() + " - ")
                    ))
                    .count();
            p.setEntrevistasRealizadas(count);
        }
    }

    public boolean estaAbaixoDaMetaAmostral(Pesquisa pesquisa, List<Entrevista> todasEntrevistas) {
        if (pesquisa == null || pesquisa.getPlanoAmostral() == null ||
                (pesquisa.getPlanoAmostral().getMetasPorGrupo().isEmpty()
                        && pesquisa.getPlanoAmostral().getMetasPorCategoria().isEmpty())) {
            return false;
        }

        String codPesquisa = pesquisa.getCodigo().trim();
        Map<String, Integer> contagemPorGrupo = new HashMap<>();
        if (todasEntrevistas != null) {
            for (Entrevista e : todasEntrevistas) {
                if (e != null && e.getCodigoPesquisa() != null && e.getParticipante() != null) {
                    String codEnt = e.getCodigoPesquisa().trim();
                    boolean mesmaPesquisa = codPesquisa.equalsIgnoreCase(codEnt)
                            || codEnt.toLowerCase().startsWith(codPesquisa.toLowerCase() + " - ");
                    if (mesmaPesquisa) {
                        Participante part = e.getParticipante();
                        incrementarGrupo(contagemPorGrupo, part.getRegiao());
                        incrementarGrupo(contagemPorGrupo, part.getFaixaEtaria());
                        incrementarGrupo(contagemPorGrupo, part.getGenero());
                        incrementarGrupo(contagemPorGrupo, part.getEscolaridade());
                        incrementarGrupo(contagemPorGrupo, part.getFaixaRenda());
                    }
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
    public List<String> obterStatusGruposPlanoAmostral(Pesquisa pesquisa, List<Entrevista> todasEntrevistas) {
        if (pesquisa == null || pesquisa.getPlanoAmostral() == null) {
            return Collections.emptyList();
        }

        PlanoAmostral plano = pesquisa.getPlanoAmostral();
        if (plano.getMetasPorCategoria().isEmpty() && plano.getMetasPorGrupo().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Integer> contagemPorGrupo = contarParticipantesPorGrupo(pesquisa, todasEntrevistas);
        List<String> detalhes = new ArrayList<>();

        if (!plano.getMetasPorCategoria().isEmpty()) {
            for (Map.Entry<String, Map<String, Integer>> catEntry : plano.getMetasPorCategoria().entrySet()) {
                String categoria = catEntry.getKey();
                for (Map.Entry<String, Integer> metaEntry : catEntry.getValue().entrySet()) {
                    String grupo = metaEntry.getKey();
                    int minimo = metaEntry.getValue();
                    int realizadas = contagemPorGrupo.getOrDefault(grupo, 0);
                    if (realizadas < minimo) {
                        detalhes.add(" [" + categoria + "] " + grupo + ": " + realizadas + "/" + minimo
                                + " (ABAIXO DA META - faltam " + (minimo - realizadas) + ")");
                    } else {
                        detalhes.add("[" + categoria + "] " + grupo + ": " + realizadas + "/" + minimo + " (Atingida)");
                    }
                }
            }
        } else {
            for (Map.Entry<String, Integer> metaEntry : plano.getMetasPorGrupo().entrySet()) {
                String grupo = metaEntry.getKey();
                int minimo = metaEntry.getValue();
                int realizadas = contagemPorGrupo.getOrDefault(grupo, 0);
                if (realizadas < minimo) {
                    detalhes.add(" " + grupo + ": " + realizadas + "/" + minimo
                            + " (ABAIXO DA META - faltam " + (minimo - realizadas) + ")");
                } else {
                    detalhes.add(" " + grupo + ": " + realizadas + "/" + minimo + " (Atingida)");
                }
            }
        }
        return detalhes;
    }

    private Map<String, Integer> contarParticipantesPorGrupo(Pesquisa pesquisa, List<Entrevista> todasEntrevistas) {
        String codPesquisa = pesquisa.getCodigo().trim();
        Map<String, Integer> contagemPorGrupo = new HashMap<>();
        if (todasEntrevistas != null) {
            for (Entrevista e : todasEntrevistas) {
                if (e != null && e.getCodigoPesquisa() != null && e.getParticipante() != null) {
                    String codEnt = e.getCodigoPesquisa().trim();
                    boolean mesmaPesquisa = codPesquisa.equalsIgnoreCase(codEnt)
                            || codEnt.toLowerCase().startsWith(codPesquisa.toLowerCase() + " - ");
                    if (mesmaPesquisa) {
                        Participante part = e.getParticipante();
                        incrementarGrupo(contagemPorGrupo, part.getRegiao());
                        incrementarGrupo(contagemPorGrupo, part.getFaixaEtaria());
                        incrementarGrupo(contagemPorGrupo, part.getGenero());
                        incrementarGrupo(contagemPorGrupo, part.getEscolaridade());
                        incrementarGrupo(contagemPorGrupo, part.getFaixaRenda());
                    }
                }
            }
        }
        return contagemPorGrupo;
    }

    // lista pesquisas
    public List<Pesquisa> listarTodas() throws PesquisaInvalidaException {
        if (pesquisas.isEmpty()) {
            throw new PesquisaInvalidaException("Nenhuma pesquisa cadastrada no sistema.");
        }
        return new ArrayList<>(pesquisas);
    }

    public List<Pesquisa> getPesquisas() {
        return new ArrayList<>(pesquisas);
    }

    // regras de ngc
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
        boolean semRegioes = (pesquisa.getRegioesAssociadas() == null || pesquisa.getRegioesAssociadas().isEmpty())
                && (pesquisa.getAbrangencia() == null || pesquisa.getAbrangencia().trim().isEmpty());
        if (semRegioes) {
            throw new PesquisaInvalidaException("Selecione pelo menos uma região de abrangência.");
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