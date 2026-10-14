package org.example.pesquisas.dominio;

import org.example.candidatos.dominio.Candidato;
import org.example.comum.Cargo;
import org.example.comum.SituacaoPesquisa;
import org.example.comum.TipoPesquisa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pesquisa eleitoral simulada (RF-PES-1 a 7).
// Tem muitos campos, por isso e criada pelo padrao Builder: Pesquisa.builder(...).periodo(...).build()
public class Pesquisa {

    private final String codigo;
    private String nome;
    private Cargo cargo;
    private TipoPesquisa tipo;
    private SituacaoPesquisa situacao = SituacaoPesquisa.PLANEJADA;
    private LocalDate inicio;
    private LocalDate fim;
    private String abrangencia;
    private int quantidadePrevista;
    private String metodologia;
    private final List<Pergunta> perguntas = new ArrayList<>();
    private final List<Candidato> candidatos = new ArrayList<>();
    private final List<String> regioes = new ArrayList<>();
    private final List<MetaAmostral> metas = new ArrayList<>();

    // so o Builder cria pesquisas
    private Pesquisa(Builder b) {
        this.codigo = b.codigo;
        this.nome = b.nome;
        this.cargo = b.cargo;
        this.tipo = b.tipo;
        this.inicio = b.inicio;
        this.fim = b.fim;
        this.abrangencia = b.abrangencia;
        this.quantidadePrevista = b.quantidadePrevista;
        this.metodologia = b.metodologia;
    }

    public static Builder builder(String codigo, String nome, Cargo cargo, TipoPesquisa tipo) {
        return new Builder(codigo, nome, cargo, tipo);
    }

    // ---------- regras de situacao ----------

    // RN-PES-2: intencao de voto precisa de pelo menos um candidato
    public void iniciarColeta() {
        exigirSituacao(SituacaoPesquisa.PLANEJADA, "iniciar a coleta");
        if (tipo == TipoPesquisa.INTENCAO_DE_VOTO && candidatos.isEmpty()) {
            throw new IllegalStateException("Uma pesquisa de intenção de voto precisa de pelo menos um candidato.");
        }
        situacao = SituacaoPesquisa.EM_COLETA;
    }

    public void encerrarColeta() {
        exigirSituacao(SituacaoPesquisa.EM_COLETA, "encerrar a coleta");
        situacao = SituacaoPesquisa.ENCERRADA;
    }

    // RN-PES-8: so processa depois de encerrada (ou reprocessa, RN-PES-9)
    public void marcarProcessada() {
        if (situacao != SituacaoPesquisa.ENCERRADA && situacao != SituacaoPesquisa.PROCESSADA) {
            throw new IllegalStateException("A pesquisa " + codigo + " só pode ser processada após o encerramento da coleta.");
        }
        situacao = SituacaoPesquisa.PROCESSADA;
    }

    // RN-PES-7: so recebe entrevistas durante a coleta
    public boolean podeReceberEntrevistas() {
        return situacao == SituacaoPesquisa.EM_COLETA;
    }

    private void exigirSituacao(SituacaoPesquisa esperada, String acao) {
        if (situacao != esperada) {
            throw new IllegalStateException("Não é possível " + acao + ": a pesquisa está " + situacao + ".");
        }
    }

    // ---------- perguntas, candidatos, regioes e metas ----------

    public void adicionarPergunta(Pergunta pergunta) {
        perguntas.add(pergunta);
    }

    public void removerPergunta(Pergunta pergunta) {
        perguntas.remove(pergunta);
    }

    // RN-PES-3 (cargo compativel), RN-PES-5 (situacao permitida) e RN-PES-6 (sem repeticao)
    public void associarCandidato(Candidato candidato) {
        if (candidato.getCargo() != cargo) {
            throw new IllegalArgumentException(candidato.getNome() + " concorre a " + candidato.getCargo()
                    + ", mas a pesquisa é para " + cargo + ".");
        }
        if (!candidato.podeParticiparDePesquisa()) {
            throw new IllegalArgumentException("A candidatura de " + candidato.getNome()
                    + " está como " + candidato.getSituacao() + " e não pode participar de pesquisas.");
        }
        if (candidatos.contains(candidato)) {
            throw new IllegalArgumentException(candidato.getNome() + " já está associado a esta pesquisa.");
        }
        candidatos.add(candidato);
    }

    public void removerCandidato(Candidato candidato) {
        candidatos.remove(candidato);
    }

    public void associarRegiao(String regiao) {
        if (!regioes.contains(regiao)) {
            regioes.add(regiao);
        }
    }

    public void removerRegiao(String regiao) {
        regioes.remove(regiao);
    }

    public void adicionarMeta(MetaAmostral meta) {
        metas.add(meta);
    }

    // ---------- getters e setters ----------

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public TipoPesquisa getTipo() {
        return tipo;
    }

    public SituacaoPesquisa getSituacao() {
        return situacao;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setPeriodo(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getAbrangencia() {
        return abrangencia;
    }

    public void setAbrangencia(String abrangencia) {
        this.abrangencia = abrangencia;
    }

    public int getQuantidadePrevista() {
        return quantidadePrevista;
    }

    // RF-PES-7
    public void setQuantidadePrevista(int quantidadePrevista) {
        if (quantidadePrevista <= 0) {
            throw new IllegalArgumentException("A quantidade prevista de entrevistas deve ser positiva.");
        }
        this.quantidadePrevista = quantidadePrevista;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public List<Pergunta> getPerguntas() {
        return Collections.unmodifiableList(perguntas);
    }

    public List<Candidato> getCandidatos() {
        return Collections.unmodifiableList(candidatos);
    }

    public List<String> getRegioes() {
        return Collections.unmodifiableList(regioes);
    }

    public List<MetaAmostral> getMetas() {
        return Collections.unmodifiableList(metas);
    }

    private static void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Período de coleta inválido.");
        }
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (" + tipo + ", " + cargo + ", " + situacao + ")";
    }

    // Padrao Builder: os campos obrigatorios vao no construtor do Builder,
    // os opcionais em metodos encadeados; build() confere tudo de uma vez (RN-PES-1).
    public static class Builder {

        private final String codigo;
        private final String nome;
        private final Cargo cargo;
        private final TipoPesquisa tipo;
        private LocalDate inicio;
        private LocalDate fim;
        private String abrangencia;
        private int quantidadePrevista;
        private String metodologia = "";

        private Builder(String codigo, String nome, Cargo cargo, TipoPesquisa tipo) {
            this.codigo = codigo;
            this.nome = nome;
            this.cargo = cargo;
            this.tipo = tipo;
        }

        public Builder periodo(LocalDate inicio, LocalDate fim) {
            this.inicio = inicio;
            this.fim = fim;
            return this;
        }

        public Builder abrangencia(String abrangencia) {
            this.abrangencia = abrangencia;
            return this;
        }

        public Builder quantidadePrevista(int quantidade) {
            this.quantidadePrevista = quantidade;
            return this;
        }

        public Builder metodologia(String metodologia) {
            this.metodologia = metodologia;
            return this;
        }

        public Pesquisa build() {
            if (codigo == null || codigo.isBlank()) {
                throw new IllegalArgumentException("A pesquisa precisa de um código.");
            }
            if (cargo == null || tipo == null) {
                throw new IllegalArgumentException("Cargo e tipo da pesquisa são obrigatórios.");
            }
            if (abrangencia == null || abrangencia.isBlank()) {
                throw new IllegalArgumentException("A abrangência da pesquisa é obrigatória.");
            }
            validarPeriodo(inicio, fim);
            Pesquisa pesquisa = new Pesquisa(this);
            pesquisa.setQuantidadePrevista(quantidadePrevista);
            return pesquisa;
        }
    }
}
