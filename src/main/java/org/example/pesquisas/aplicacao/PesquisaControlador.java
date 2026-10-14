package org.example.pesquisas.aplicacao;

import org.example.candidatos.dominio.Candidato;
import org.example.comum.Cargo;
import org.example.comum.SituacaoPesquisa;
import org.example.comum.TipoPesquisa;
import org.example.pesquisas.dominio.AndamentoPesquisa;
import org.example.pesquisas.dominio.MetaAmostral;
import org.example.pesquisas.dominio.Pergunta;
import org.example.pesquisas.dominio.Pesquisa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Casos de uso de pesquisas: cadastro, configuracao, consulta e acompanhamento.
public class PesquisaControlador {

    private final List<Pesquisa> pesquisas = new ArrayList<>();

    // RF-PES-1 e RN-PES-1: codigo unico
    public void cadastrar(Pesquisa pesquisa) {
        if (buscarPorCodigo(pesquisa.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe uma pesquisa com o código " + pesquisa.getCodigo() + ".");
        }
        pesquisas.add(pesquisa);
    }

    public void excluir(Pesquisa pesquisa) {
        pesquisas.remove(pesquisa);
    }

    public List<Pesquisa> listar() {
        return new ArrayList<>(pesquisas);
    }

    public Pesquisa buscarPorCodigo(String codigo) {
        for (Pesquisa p : pesquisas) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

    // RF-PES-4, 5 e 6: configuracao da pesquisa
    public void adicionarPergunta(Pesquisa pesquisa, Pergunta pergunta) {
        pesquisa.adicionarPergunta(pergunta);
    }

    public void associarRegiao(Pesquisa pesquisa, String regiao) {
        pesquisa.associarRegiao(regiao);
    }

    public void associarCandidato(Pesquisa pesquisa, Candidato candidato) {
        pesquisa.associarCandidato(candidato);
    }

    // RF-PES-3: mudancas de situacao
    public void iniciarColeta(Pesquisa pesquisa) {
        pesquisa.iniciarColeta();
    }

    public void encerrarColeta(Pesquisa pesquisa) {
        pesquisa.encerrarColeta();
    }

    // RF-PES-8: filtros nulos sao ignorados; o periodo filtra pesquisas que se sobrepoem a ele
    public List<Pesquisa> consultar(Cargo cargo, TipoPesquisa tipo, SituacaoPesquisa situacao, String regiao,
                                    LocalDate inicio, LocalDate fim, Integer minimoEntrevistados) {
        List<Pesquisa> encontradas = new ArrayList<>();
        for (Pesquisa p : pesquisas) {
            boolean ok = (cargo == null || p.getCargo() == cargo)
                    && (tipo == null || p.getTipo() == tipo)
                    && (situacao == null || p.getSituacao() == situacao)
                    && (regiao == null || p.getRegioes().contains(regiao))
                    && (inicio == null || !p.getFim().isBefore(inicio))
                    && (fim == null || !p.getInicio().isAfter(fim))
                    && (minimoEntrevistados == null || p.getQuantidadePrevista() >= minimoEntrevistados);
            if (ok) {
                encontradas.add(p);
            }
        }
        return encontradas;
    }

    // RF-PES-9: a quantidade realizada vem do modulo de coleta
    public AndamentoPesquisa calcularAndamento(Pesquisa pesquisa, int entrevistasRealizadas) {
        return new AndamentoPesquisa(pesquisa.getQuantidadePrevista(), entrevistasRealizadas);
    }

    // RF-PES-10 e RN-PES-10: recebe quantas entrevistas validas cada grupo teve
    // (chave = valor da caracteristica, ex.: "Norte") e devolve as metas nao atingidas
    public List<MetaAmostral> metasNaoAtingidas(Pesquisa pesquisa, Map<String, Integer> entrevistasPorGrupo) {
        List<MetaAmostral> naoAtingidas = new ArrayList<>();
        for (MetaAmostral meta : pesquisa.getMetas()) {
            int realizadas = entrevistasPorGrupo.getOrDefault(meta.getValor(), 0);
            if (!meta.atingidaPor(realizadas)) {
                naoAtingidas.add(meta);
            }
        }
        return naoAtingidas;
    }
}
