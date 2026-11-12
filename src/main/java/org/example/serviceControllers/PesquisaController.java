package org.example.serviceControllers;

import org.example.exceptions.PersistenciaException;
import org.example.exceptions.PesquisaInvalidaException;
import org.example.models.comum.Cargo;
import org.example.models.researchModels.Pesquisa;
import org.example.models.researchModels.PesquisaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PesquisaController {
    private List<Pesquisa> pesquisas;
    private final PesquisaRepository repository;

    // Listas de domínios restritos alinhadas aos requisitos
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

    // --- SALVAR / CADASTRAR ---
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

        pesquisas.add(pesquisa);
        repository.salvarTodas(pesquisas);
    }

    // --- EDITAR / ATUALIZAR ---
    public void atualizar(String codigo, Pesquisa pesquisaAtualizada) throws PesquisaInvalidaException, PersistenciaException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new PesquisaInvalidaException("Código é obrigatório para atualização.");
        }

        Pesquisa pesquisaExistente = buscarPorCodigo(codigo);
        validarCamposEditaveis(pesquisaAtualizada);

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

    // --- BUSCA COM FILTROS COMBINADOS (AMPLA) ---
    public List<Pesquisa> buscarComFiltros(String termo, Cargo cargo, String tipo, String situacao, String abrangencia, LocalDate dataInicio, LocalDate dataFim) {
        return pesquisas.stream()
                .filter(p -> (termo == null || termo.trim().isEmpty() ||
                        p.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        p.getCodigo().equalsIgnoreCase(termo)))
                .filter(p -> (cargo == null || p.getCargo() == cargo))
                .filter(p -> (tipo == null || tipo.trim().isEmpty() || p.getTipo().equalsIgnoreCase(tipo)))
                .filter(p -> (situacao == null || situacao.trim().isEmpty() || p.getSituacao().equalsIgnoreCase(situacao)))
                .filter(p -> (abrangencia == null || abrangencia.trim().isEmpty() || p.getAbrangencia().toLowerCase().contains(abrangencia.toLowerCase())))
                .filter(p -> (dataInicio == null || (p.getDataInicio() != null && !p.getDataInicio().isBefore(dataInicio))))
                .filter(p -> (dataFim == null || (p.getDataFim() != null && !p.getDataFim().isAfter(dataFim))))
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

    // --- DELETAR ---
    public void deletar(String codigo) throws PesquisaInvalidaException, PersistenciaException {
        Pesquisa pesquisaParaRemover = buscarPorCodigo(codigo);
        pesquisas.remove(pesquisaParaRemover);
        repository.salvarTodas(pesquisas);
    }

    // --- LISTAR TODAS ---
    public List<Pesquisa> listarTodas() throws PesquisaInvalidaException {
        if (pesquisas.isEmpty()) {
            throw new PesquisaInvalidaException("Nenhuma pesquisa cadastrada no sistema.");
        }
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