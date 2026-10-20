package org.example.models.candidatos;

//herda o numero de RegistroEleitoral
public class Partido extends RegistroEleitoral {

    private String nome;
    private String sigla;
    private SituacaoPartido situacao;

    public Partido(String nome, String sigla, int numero, SituacaoPartido situacao) {
        super(numero);
        setNome(nome);
        setSigla(sigla);
        setSituacao(situacao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do partido é obrigatório.");
        }
        this.nome = nome.trim();
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        if (sigla == null || sigla.isBlank()) {
            throw new IllegalArgumentException("A sigla do partido é obrigatória.");
        }
        this.sigla = sigla.trim().toUpperCase();
    }

    public SituacaoPartido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoPartido situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("A situação do partido é obrigatória.");
        }
        this.situacao = situacao;
    }

    //so conta como ativo se a situacao for ATIVO
    @Override
    public boolean isAtivo() {
        return situacao == SituacaoPartido.ATIVO;
    }

    @Override
    public String getDescricao() {
        return sigla + " - " + nome + " (" + getNumero() + ")";
    }
}