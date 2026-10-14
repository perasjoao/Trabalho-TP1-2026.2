package org.example.analise.dominio;

import org.example.coleta.dominio.Entrevista;
import org.example.coleta.dominio.Participante;
import org.example.pesquisas.dominio.Pesquisa;

import java.time.LocalDate;

// Filtros de um relatorio (RF-ANA-7). Quase todos sao opcionais,
// entao o filtro e montado pelo padrao Builder:
// FiltroRelatorio.para(pesquisa).regiao("Norte").faixaEtaria("16-24").build()
public class FiltroRelatorio {

    private final Pesquisa pesquisa;
    private final LocalDate inicio;
    private final LocalDate fim;
    private final String regiao;
    private final String candidato;
    private final String faixaEtaria;
    private final String escolaridade;
    private final String genero;
    private final String faixaRenda;

    private FiltroRelatorio(Builder b) {
        this.pesquisa = b.pesquisa;
        this.inicio = b.inicio;
        this.fim = b.fim;
        this.regiao = b.regiao;
        this.candidato = b.candidato;
        this.faixaEtaria = b.faixaEtaria;
        this.escolaridade = b.escolaridade;
        this.genero = b.genero;
        this.faixaRenda = b.faixaRenda;
    }

    public static Builder para(Pesquisa pesquisa) {
        return new Builder(pesquisa);
    }

    // a entrevista entra no relatorio se passar por todos os filtros preenchidos
    public boolean aceita(Entrevista e) {
        Participante p = e.getParticipante();
        return e.getPesquisa() == pesquisa
                && (inicio == null || e.getData() == null || !e.getData().isBefore(inicio))
                && (fim == null || e.getData() == null || !e.getData().isAfter(fim))
                && igualOuIgnorado(regiao, p.getRegiao())
                && igualOuIgnorado(faixaEtaria, p.getFaixaEtaria())
                && igualOuIgnorado(escolaridade, p.getEscolaridade())
                && igualOuIgnorado(genero, p.getGenero())
                && igualOuIgnorado(faixaRenda, p.getFaixaRenda());
    }

    private static boolean igualOuIgnorado(String filtro, String valor) {
        return filtro == null || filtro.equalsIgnoreCase(valor);
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    // o filtro de candidato limita as linhas do resultado, nao as entrevistas
    public String getCandidato() {
        return candidato;
    }

    // texto dos filtros usados, para o cabecalho do relatorio
    public String descrever() {
        StringBuilder sb = new StringBuilder("Pesquisa " + pesquisa.getCodigo());
        acrescentar(sb, "período", inicio == null && fim == null ? null : inicio + " a " + fim);
        acrescentar(sb, "região", regiao);
        acrescentar(sb, "candidato", candidato);
        acrescentar(sb, "faixa etária", faixaEtaria);
        acrescentar(sb, "escolaridade", escolaridade);
        acrescentar(sb, "gênero", genero);
        acrescentar(sb, "faixa de renda", faixaRenda);
        return sb.toString();
    }

    private static void acrescentar(StringBuilder sb, String nome, String valor) {
        if (valor != null) {
            sb.append("; ").append(nome).append(": ").append(valor);
        }
    }

    public static class Builder {

        private final Pesquisa pesquisa;
        private LocalDate inicio;
        private LocalDate fim;
        private String regiao;
        private String candidato;
        private String faixaEtaria;
        private String escolaridade;
        private String genero;
        private String faixaRenda;

        private Builder(Pesquisa pesquisa) {
            if (pesquisa == null) {
                throw new IllegalArgumentException("O relatório precisa de uma pesquisa.");
            }
            this.pesquisa = pesquisa;
        }

        public Builder periodo(LocalDate inicio, LocalDate fim) {
            this.inicio = inicio;
            this.fim = fim;
            return this;
        }

        public Builder regiao(String regiao) {
            this.regiao = regiao;
            return this;
        }

        public Builder candidato(String candidato) {
            this.candidato = candidato;
            return this;
        }

        public Builder faixaEtaria(String faixaEtaria) {
            this.faixaEtaria = faixaEtaria;
            return this;
        }

        public Builder escolaridade(String escolaridade) {
            this.escolaridade = escolaridade;
            return this;
        }

        public Builder genero(String genero) {
            this.genero = genero;
            return this;
        }

        public Builder faixaRenda(String faixaRenda) {
            this.faixaRenda = faixaRenda;
            return this;
        }

        public FiltroRelatorio build() {
            if (inicio != null && fim != null && fim.isBefore(inicio)) {
                throw new IllegalArgumentException("A data final do filtro é anterior à inicial.");
            }
            return new FiltroRelatorio(this);
        }
    }
}
